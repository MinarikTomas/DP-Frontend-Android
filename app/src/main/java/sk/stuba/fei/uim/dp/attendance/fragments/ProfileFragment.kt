package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.adapters.CardItem
import sk.stuba.fei.uim.dp.attendance.adapters.CardsAdapter
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentProfileBinding
import sk.stuba.fei.uim.dp.attendance.utils.SpacesItemDecoration
import sk.stuba.fei.uim.dp.attendance.viewmodels.ProfileViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile){

    private var binding: FragmentProfileBinding?= null
    private lateinit var viewModel: ProfileViewModel
    private var deletedCardPosition = -1
    private var updatedCardPosition = -1
    private lateinit var updatedCard: CardItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
        val user = PreferenceData.getInstance().getUser(requireContext())
        viewModel.getCards(user?.id ?: -1)
        viewModel.fullName.value = user?.name
        viewModel.email.value = user?.email
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            viewModel.getCards(PreferenceData.getInstance().getUser(requireContext())?.id ?: -1)

            val recyclerView = bnd.recyclerviewCards
            recyclerView.layoutManager = LinearLayoutManager(context)
            val cardsAdapter = CardsAdapter()
            recyclerView.adapter = cardsAdapter
            recyclerView.addItemDecoration(
                SpacesItemDecoration(10)
            )
            cardsAdapter.setOnClickDeleteListener(object: CardsAdapter.OnClickListener{
                override fun onClick(position: Int, model: CardItem) {
                    viewModel.deactivateCard(model.id)
                    deletedCardPosition = position
                }
            })
            cardsAdapter.setOnClickEditSaveListener(object: CardsAdapter.OnClickListener{
                override fun onClick(position: Int, model: CardItem) {
                    viewModel.updateCard(model.id, model.name)
                    updatedCard = model
                    updatedCardPosition = position
                }
            })

            bnd.btnLogout.apply {
                setOnClickListener {
                    PreferenceData.getInstance().clearData(requireContext())
                    it.findNavController().navigate(R.id.action_profile_login)
                }
            }

            bnd.addCard.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_profile_add_card)
                }
            }

            bnd.attendedActivities.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_profile_attended_activities)
                }
            }

            bnd.changePassword.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_profile_change_password)
                }
            }

            viewModel.getCardsResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if (it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_logout),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.deactivateResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if (it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_logout),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.updateCardResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_logout),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.cards.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        cardsAdapter.updateItems((it.map { CardItem(it.name, it.id) }).toMutableList())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}