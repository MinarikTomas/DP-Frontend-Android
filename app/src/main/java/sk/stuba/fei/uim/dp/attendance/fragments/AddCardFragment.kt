package sk.stuba.fei.uim.dp.attendance.fragments

import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentAddCardBinding
import sk.stuba.fei.uim.dp.attendance.viewmodels.AuthViewModel

class AddCardFragment : Fragment(R.layout.fragment_add_card) {

    private var binding: FragmentAddCardBinding ?= null
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddCardBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also {bnd ->

           bnd.btnScan.apply {
               setOnClickListener {
                   val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

                   nfcAdapter?.enableReaderMode(
                       requireActivity(),
                       NfcAdapter.ReaderCallback { tag ->
                           Log.d("NfcActivity", "tag discovered")
                           Log.d("NfcActivity", tag.id.toHex())
                           viewModel.signupUser(tag.id.toHex())
                       },
                       NfcAdapter.FLAG_READER_NFC_A or
                               NfcAdapter.FLAG_READER_NFC_B or
                               NfcAdapter.FLAG_READER_NFC_F or
                               NfcAdapter.FLAG_READER_NFC_V,
                       null
                   )
               }
           }

            viewModel.signupResult.observe(viewLifecycleOwner){
                if(it.isNotEmpty()){
                    Snackbar.make(
                        view.findViewById(R.id.btn_scan),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }else{
                    Snackbar.make(
                        view.findViewById(R.id.btn_scan),
                        "Successfully signed up",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    requireView().findNavController().navigate(R.id.action_add_card_to_login)
                }
            }
        }
    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

}