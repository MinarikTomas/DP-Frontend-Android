package sk.stuba.fei.uim.dp.attendance.fragments

import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
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

class ActivityFragment : Fragment(R.layout.fragment_activity) {

    private var binding: FragmentActivityBinding?= null
    private lateinit var viewModel: ActivityViewModel

    private val args: ActivityFragmentArgs by navArgs()
    private  var activityReceived = false
    private var activity: Activity? = null

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
                if(it.isNotEmpty()){
                    Snackbar.make(
                        view.findViewById(R.id.title),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.activityResult.observe(viewLifecycleOwner){ it ->
                it.getContentIfNotHandled()?.let {
                    activity = it
                    activityReceived = true
                    Log.d("ActivityFragment", it.name)
                    // activity is running
                    if(it.startTime != "" && it.endTime == ""){
                        handleNFC()
                        bnd.btnEnd.visibility = View.VISIBLE
                        // activity has not started yet
                    }else if(it.startTime == ""){
                        bnd.btnStart.visibility = View.VISIBLE
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
                activity?.let {
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

            viewModel.startActivityResult.observe(viewLifecycleOwner){
                if(!activityReceived) return@observe
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

            viewModel.endActivityResult.observe(viewLifecycleOwner){
                if(!activityReceived) return@observe
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

            viewModel.addParticipantResult.observe(viewLifecycleOwner){
                if(it.isNotEmpty()){
                    Snackbar.make(
                        view.findViewById(R.id.title),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.participantResult.observe(viewLifecycleOwner){
                it?.let {
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

    override fun onPause() {
        super.onPause()
        activityReceived = false
        viewModel.clearActivity()
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
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V,
            null)
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}