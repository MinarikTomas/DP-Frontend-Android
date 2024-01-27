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
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentAddCardBinding
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentProfileBinding
import sk.stuba.fei.uim.dp.attendance.viewmodels.ProfileViewModel

class AddCardFragment : Fragment(R.layout.fragment_add_card) {

    private var binding: FragmentAddCardBinding ?= null
    private lateinit var viewModel: ProfileViewModel
    private lateinit var serialNumber: String
    private var cardAdded = false

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
                            Log.d("AddCardFragment", tag.id.toHex())
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
                    viewModel.addCard(
                        PreferenceData.getInstance().getUser(requireContext())?.id ?: -1,
                        serialNumber
                    )
                }
            }

            viewModel.addCardResult.observe(viewLifecycleOwner){
                if(!cardAdded) return@observe
                if(it.isNotEmpty()){
                    Snackbar.make(
                        view.findViewById(R.id.btn_scan),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    cardAdded = false
                }else{
                    cardAdded = true
                    viewModel.cardName.value = ""
                    requireView().findNavController().navigate(R.id.action_add_card_profile)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        cardAdded = false
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}