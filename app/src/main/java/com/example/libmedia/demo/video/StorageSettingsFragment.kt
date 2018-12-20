package com.example.libmedia.demo.video

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import fr.maxcom.os.storage.Volume
import fr.maxcom.os.storage.VolumeManager

class StorageSettingsFragment : ListFragment(), OnClickListener {

    private lateinit var mVolumeManager: VolumeManager
    private var mRequestStorageAccessIntent: Intent? = null
    private lateinit var mAdapter: ItemAdapter<Volume>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mVolumeManager = VolumeManager()
        // A null means that Storage Access Framework is not activated.
        mRequestStorageAccessIntent = VolumeManager.getStorageAccessIntent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_storage, container, false)
        val btn = v.findViewById<Button>(R.id.btnAddVolume)
        if (mRequestStorageAccessIntent != null) {
            btn.setOnClickListener(this)
        } else {
            // The button is not applicable in this running context, hide it.
            btn.visibility = View.GONE
        }
        val volumes = mVolumeManager.volumes
        mAdapter = ItemAdapter(activity as Context, R.layout.list_volume, volumes)
        listAdapter = mAdapter
        return v
    }

    //----- OnClickListener Interface -----
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAddVolume -> requestStorageAccess()
        }
    }

    private inner class ItemAdapter<T : Volume> constructor(
        context: Context, private val mResource: Int, objects: List<T>
    ) : ArrayAdapter<T>(context, mResource, objects),
        OnClickListener {

        private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: mInflater.inflate(mResource, parent, false).apply {
                findViewById<View>(R.id.btnEditLabel).setOnClickListener(this@ItemAdapter)
                findViewById<View>(R.id.btnRemoveLabel).setOnClickListener(this@ItemAdapter)
                findViewById<View>(R.id.btnForget).setOnClickListener(this@ItemAdapter)
            }
            val item = getItem(position)
            bindView(view, item!!)
            return view
        }

        @SuppressLint("SetTextI18n")
        private fun bindView(view: View, item: T) {
            val label = item.label
            val optName = if (item.name != null && item.name != label) " (${item.name})" else ""
            (view.findViewById<View>(R.id.txLabel) as TextView).text = "$label$optName"
            view.findViewById<View>(R.id.btnRemoveLabel).visibility = if (item.hasLabelFile()) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.btnForget).visibility = if (item.isForgettable) View.VISIBLE else View.GONE
        }

        //----- onClick Helpers -----
        private fun getTargetVolume(v: View): Volume? {
            val position = listView.getPositionForView(v)
            return if (position != AdapterView.INVALID_POSITION) {
                mAdapter.getItem(position)
            } else null
        }

        //----- OnClickListener Interface -----
        override fun onClick(v: View) {
            val volume = getTargetVolume(v)
            if (volume != null) {
                when (v.id) {
                    R.id.btnEditLabel -> {
                        val f = EditLabelDialogFragment.newInstance(mAdapter.getPosition(volume), volume.label)
                        f.setTargetFragment(this@StorageSettingsFragment, 0)
                        f.show(fragmentManager!!, "EditLabelDialogFragment")
                    }
                    R.id.btnRemoveLabel -> if (volume.deleteLabelFile()) refreshVolumes()
                    R.id.btnForget -> if (volume.forget()) renewVolumes()
                }
            }
        }
    }

    //----- Processors -----
    private fun requestStorageAccess() {
        startActivityForResult(mRequestStorageAccessIntent, STORAGE_ACCESS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == STORAGE_ACCESS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Forward the result, to persist the selection.
            VolumeManager.onStorageAccessResult(resultData)
            // The list needs to be fully refreshed.
            renewVolumes()
        }
    }

    class EditLabelDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val args = arguments
            val position = args!!.getInt("position")
            val oldVal = args.getString("value")
            @SuppressLint("InflateParams") val v = activity!!.layoutInflater.inflate(R.layout.dialog_label, null)
            (v.findViewById<View>(R.id.edtLabel) as EditText).setText(oldVal)
            return AlertDialog.Builder(activity)
                    .setTitle("Label")
                    .setView(v)
                    .setPositiveButton("OK") { _, _ ->
                        val newVal = (this@EditLabelDialogFragment.dialog.findViewById<View>(R.id.edtLabel) as EditText)
                                .text.toString()
                        if (newVal != oldVal) {
                            (targetFragment as StorageSettingsFragment).onDialogLabelChange(position, newVal)
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ -> this@EditLabelDialogFragment.dialog.cancel() }
                    .create()
        }

        companion object {
            internal fun newInstance(position: Int, value: String?): EditLabelDialogFragment {
                val f = EditLabelDialogFragment()
                val args = Bundle()
                args.putInt("position", position)
                args.putString("value", value)
                f.arguments = args
                return f
            }
        }
    }

    // Called on the exit from the dialog, if the label has changed.
    private fun onDialogLabelChange(position: Int, label: String) {
        val volume = mAdapter.getItem(position)
        if (volume != null) {
            volume.writeLabelFile(label)
            refreshVolumes()
        }
    }

    // There has been an addition or a subtraction of a Volume, request an updated list.
    private fun renewVolumes() {
        mAdapter.clear()
        mAdapter.addAll(mVolumeManager.volumes)
        refreshVolumes()
    }

    // There has been a change in the properties of at least one of the items, request an updated display.
    private fun refreshVolumes() {
        mAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val STORAGE_ACCESS_REQUEST_CODE = 99  // any value of your choice
    }
}
