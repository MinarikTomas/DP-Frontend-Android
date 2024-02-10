package sk.stuba.fei.uim.dp.attendance.fragments

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentAddCardSignupBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.SignupViewModel

class AddCardSignupFragment : Fragment(R.layout.fragment_add_card_signup) {

    private var binding: FragmentAddCardSignupBinding ?= null
    private lateinit var viewModel: SignupViewModel
    private lateinit var serialNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignupViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[SignupViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddCardSignupBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also {bnd ->
            if (NfcAdapter.getDefaultAdapter(requireContext()) == null){
                Snackbar.make(
                    view.findViewById(R.id.btn_scan),
                    "NFC not available",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

           bnd.btnScan.apply {
               setOnClickListener {
                   val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
                   nfcAdapter?.enableReaderMode(
                       requireActivity(),
                       { tag ->
                           serialNumber = tag.id.toHex()
                           nfcAdapter.disableReaderMode(requireActivity())
                           bnd.btnScan.visibility = View.INVISIBLE
                           bnd.done.visibility = View.VISIBLE
                       },
                       NfcAdapter.FLAG_READER_NFC_A or
                               NfcAdapter.FLAG_READER_NFC_B or
                               NfcAdapter.FLAG_READER_NFC_F or
                               NfcAdapter.FLAG_READER_NFC_V,
                       null
                   )
               }
           }

            bnd.btnSignup.setOnClickListener {
                if(isInputValid()){
                    viewModel.signupUser(serialNumber)
                }
            }

            viewModel.signupResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_scan),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        NfcAdapter.getDefaultAdapter(requireContext()).disableReaderMode(requireActivity())
                        Snackbar.make(
                            view.findViewById(R.id.btn_scan),
                            "Successfully signed up",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        requireView().findNavController().navigate(R.id.action_add_card_to_login)
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
        NfcAdapter.getDefaultAdapter(requireContext())?.disableReaderMode(requireActivity())
        binding = null
        super.onDestroyView()
    }
    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

}