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
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentAddCardBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.ProfileViewModel

class AddCardFragment : Fragment(R.layout.fragment_add_card) {

    private var binding: FragmentAddCardBinding ?= null
    private lateinit var viewModel: ProfileViewModel
    private lateinit var serialNumber: String
    private val args: AddCardFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PreferenceData.getInstance().getUser(requireContext()) == null && args.user == null) {
            requireView().findNavController().navigate(R.id.action_to_login)
        }
        if (args.user == null) {
            Log.d("AddCard", "adding card")
        } else {
            Log.d("AddCard", "from login")
        }
        binding = FragmentAddCardBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.btnScan.apply {
                setOnClickListener {
                    val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

                    nfcAdapter?.enableReaderMode(
                        requireActivity(),
                        { tag ->
                            serialNumber = tag.id.toHex()
                            nfcAdapter.disableReaderMode(requireActivity())
                        },
                        NfcAdapter.FLAG_READER_NFC_A or
                                NfcAdapter.FLAG_READER_NFC_B or
                                NfcAdapter.FLAG_READER_NFC_F or
                                NfcAdapter.FLAG_READER_NFC_V,
                        null
                    )
                }
            }

            bnd.btnSaveCard.apply {
                setOnClickListener {
                    if (isInputValid()) {
                        viewModel.addCard(
                            when (args.user == null) {
                                true -> PreferenceData.getInstance().getUser(requireContext())?.id
                                    ?: -1

                                false -> args.user!!.id
                            },
                            serialNumber
                        )
                    }
                }
            }

            viewModel.addCardResult.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    if (it.isNotEmpty()) {
                        Snackbar.make(
                            view.findViewById(R.id.btn_scan),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        if(args.user != null){
                            PreferenceData.getInstance().putUser(requireContext(), args.user)
                        }
                        viewModel.cardName.value = ""
                        requireView().findNavController().navigate(R.id.action_add_card_profile)
                    }
                }
            }
            bnd.textInputLayoutCardName.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutCardName)
            )
        }
    }


    private fun isInputValid(): Boolean{
        var isValid = true
        if(viewModel.cardName.value.isNullOrEmpty()){
            binding!!.textInputLayoutCardName.isErrorEnabled = true
            binding!!.textInputLayoutCardName.error = "Cannot be empty"
            isValid = false
        }
        if(!this::serialNumber.isInitialized){
            Snackbar.make(
                requireView(),
                "Card has not been scanned yet",
                Snackbar.LENGTH_SHORT
            ).show()
            isValid = false
        }
        return isValid
    }
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}