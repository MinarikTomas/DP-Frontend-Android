package sk.stuba.fei.uim.dp.attendance.fragments

import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.adapters.ParticipantItem
import sk.stuba.fei.uim.dp.attendance.adapters.ParticipantsAdapter
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.data.model.ParcelableActivity
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentActivityBinding
import sk.stuba.fei.uim.dp.attendance.viewmodels.ActivityViewModel
import java.io.File

class ActivityFragment : Fragment(R.layout.fragment_activity) {

    private var binding: FragmentActivityBinding?= null
    private lateinit var viewModel: ActivityViewModel

    private val args: ActivityFragmentArgs by navArgs()
    private var selectedActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActivityViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ActivityViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PreferenceData.getInstance().getUser(requireContext()) == null){
            requireView().findNavController().navigate(R.id.action_to_login)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireView().findNavController().navigate(R.id.action_activity_home)
        }

    binding = FragmentActivityBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd->
            viewModel.clearBinds()
            viewModel.getActivity(args.activityId)

            val recyclerView = bnd.participantsRecyclerview
            recyclerView.layoutManager = LinearLayoutManager(context)
            val participantsAdapter = ParticipantsAdapter()
            recyclerView.adapter = participantsAdapter

            viewModel.getActivityResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.title),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.activityResult.observe(viewLifecycleOwner){ it ->
                it.getContentIfNotHandled()?.let {
                    selectedActivity = it
                    Log.d("ActivityFragment", it.name)
                    if(it.startTime != "" && it.endTime == ""){
                        // activity is running
                        handleNFC()
                        bnd.btnEnd.visibility = View.VISIBLE
                    }else if(it.startTime == ""){
                        // activity has not started yet
                        bnd.btnStart.visibility = View.VISIBLE
                    }else{
                        // activity has ended
                        bnd.btnExport.visibility = View.VISIBLE
                    }
                    val items = it.participants?.map {
                        ParticipantItem(
                            it?.name ?: "Unknown"
                        )
                    }
                    participantsAdapter.updateItems(items?.toMutableList() ?: mutableListOf())
                }
            }

            bnd.btnStart.apply {
                setOnClickListener {
                    if(NfcAdapter.getDefaultAdapter(requireContext()) == null){
                        Snackbar.make(
                            view.findViewById(R.id.title),
                            "NFC not available",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    if(!PreferenceData.getInstance().getIsActivityRunning(requireContext())){
                        viewModel.startActivity(args.activityId)
                    }else{
                        Snackbar.make(
                            view.findViewById(R.id.title),
                            "There is already a running activity",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            bnd.btnEnd.apply {
                setOnClickListener {
                    viewModel.endActivity(
                        args.activityId,
                        PreferenceData.getInstance().getUser(requireContext())?.id ?: -1)
                }
            }

            bnd.delete.apply {
                setOnClickListener {
                    viewModel.deleteActivity(
                        args.activityId,
                        PreferenceData.getInstance().getUser(requireContext())?.id ?: -1
                    )
                }
            }

            bnd.edit.setOnClickListener {
                selectedActivity?.let {
                    requireView().findNavController().navigate(ActivityFragmentDirections.actionActivityToEdit(
                        ParcelableActivity(
                            it.id,
                            it.name,
                            it.location,
                            it.date,
                            it.time
                        )
                    ))
                }
            }

        bnd.btnExport.setOnClickListener {
            val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val fileName =
                selectedActivity!!.name.replace(" ", "_") +
                        selectedActivity!!.date.replace(".", "") +
                        selectedActivity!!.time.replace(":", "")
            try{
                val file = File(documentsDir, fileName + ".json")
                file.bufferedWriter().use { out ->
                    out.write(selectedActivity!!.toJson())
                    out.close()
                    Toast.makeText(
                        requireContext(),
                        "File saved",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Failed to save file",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

            viewModel.startActivityResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.title),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        bnd.btnStart.visibility = View.INVISIBLE
                        bnd.btnEnd.visibility = View.VISIBLE
                        PreferenceData.getInstance().putIsActivityRunning(requireContext(), true)
                        handleNFC()
                    }
                }
            }

            viewModel.endActivityResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.title),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        bnd.btnEnd.visibility = View.INVISIBLE
                        PreferenceData.getInstance().putIsActivityRunning(requireContext(), false)
                        NfcAdapter.getDefaultAdapter(requireContext()).disableReaderMode(requireActivity())
                    }
                }
            }

            viewModel.addParticipantResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.title),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.participantResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    selectedActivity?.participants?.add(it)
                    participantsAdapter.addItem(ParticipantItem(it.name ?: ""))
                }
            }

            viewModel.deleteActivityResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if (it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.title),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        requireView().findNavController().navigate(R.id.action_activity_home)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        NfcAdapter.getDefaultAdapter(requireContext())?.disableReaderMode(requireActivity())
        binding = null
        super.onDestroyView()
    }

    private fun handleNFC(){
        val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if(nfcAdapter == null){
            Snackbar.make(
                requireView(),
                "NFC not available",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        nfcAdapter.enableReaderMode(
            requireActivity(),
            { tag ->
                Log.d("ActivityFragment", tag.id.toHex())
                viewModel.addParticipant(
                    args.activityId,
                    tag.id.toHex())
            },
            NfcAdapter.FLAG_READER_NFC_A,
            null)
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}