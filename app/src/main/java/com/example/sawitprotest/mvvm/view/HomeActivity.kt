package com.example.sawitprotest.mvvm.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.ActivityHomeBinding
import com.example.sawitprotest.mvvm.adapter.TicketListAdapter
import com.example.sawitprotest.mvvm.data.Ticket
import com.example.sawitprotest.mvvm.viewmodel.TicketViewModel
import com.example.sawitprotest.util.NetworkUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val viewModel: TicketViewModel by viewModels()
    private lateinit var adapter: TicketListAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        bindView()
        observeData()
        firstCall()
        syncData()
    }

    private fun bindView() {
        with(binding){
            initRecyclerView()
            customActionBar.tvTitle.text = getString(R.string.list_ticket)
            customActionBar.ivBack.setImageResource(R.drawable.logo_sawitpro_32)
            buttonAdd.setOnClickListener {
                val intent = Intent(this@HomeActivity, AddTicketActivity::class.java)
                startForResult.launch(intent)
            }
            tvFilter.setOnClickListener {
                showFilterDialog()
            }
            ivFilter.setOnClickListener {
                showFilterDialog()
            }
            searchView.setOnQueryTextListener(object : OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        adapter.filter(it)
                    }
                    return true
                }

            })
        }
    }

    private fun initRecyclerView() {
        with(binding){
            adapter = TicketListAdapter(binding.root.context)
            rvListTicket.setHasFixedSize(true)
            rvListTicket.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.VERTICAL, false
            )

            rvListTicket.adapter = adapter

            adapter.setOnItemCallback(object : TicketListAdapter.OnItemCallback{
                override fun onItemSeeDetail(ticket: Ticket) {
                    val intent = Intent(this@HomeActivity, DetailTicketActivity::class.java)
                    intent.putExtra("ticket", ticket)
                    startActivity(intent)
                }

                override fun onItemEdit(ticket: Ticket) {
                    val intent = Intent(this@HomeActivity, AddTicketActivity::class.java)
                    intent.putExtra("ticket", ticket)
                    startForResult.launch(intent)
                }

                override fun onItemAddNew() {
                    val intent = Intent(this@HomeActivity, AddTicketActivity::class.java)
                    startForResult.launch(intent)
                }

            })
        }
    }

    private fun firstCall() {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllTickets()
        }
    }

    private fun syncData() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            showSyncProgreess()
            syncDataWithFirebase()
        } else {
            Toast.makeText(binding.root.context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "SyncActivity"
    }

    private fun syncDataWithFirebase() {
        databaseReference = FirebaseDatabase.getInstance().reference.child("tickets")
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllTicketsAndSync()
        }
        viewModel.ticketsLiveDataForSync.observe(this@HomeActivity) {tickets ->
            CoroutineScope(Dispatchers.IO).launch {
                val remoteData = fetchRemoteData()

                compareAndSyncData(tickets, remoteData)
            }
        }
    }

    private fun observeData() {
        with(binding){
            viewModel.ticketsLiveData.observe(this@HomeActivity) {tickets ->
                if (tickets.isNotEmpty()) {
                    adapter.updateAdapter(tickets as ArrayList)
                    rvListTicket.visibility = View.VISIBLE
                    tvNoData.visibility = View.GONE
                    buttonAdd.visibility = View.GONE
                } else {
                    rvListTicket.visibility = View.GONE
                    tvNoData.visibility = View.VISIBLE
                    buttonAdd.visibility = View.VISIBLE
                }
            }
        }
    }

    private suspend fun compareAndSyncData(
        localData: List<Ticket>,
        remoteData: List<Ticket>
    ) {
        if (localData.size > remoteData.size) {
            syncLocalToRemote(localData, remoteData)
        } else {
            syncRemoteToLocal(localData, remoteData)
        }
    }

    private suspend fun syncLocalToRemote(
        localData: List<Ticket>,
        remoteData: List<Ticket>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        withContext(dispatcher) {
            try {
                for (ticket in localData) {
                    val ticketExistsRemotely = remoteData.any { it.id == ticket.id }
                    if (!ticketExistsRemotely) {
                        databaseReference.child(ticket.id.toString()).setValue(ticket).await()
                    }
                }

                showSyncResult(isSuccess = true)
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing local data to remote: ${e.message}", e)
                showSyncResult(isSuccess = false)
            }
        }
    }

    private suspend fun syncRemoteToLocal(
        localData: List<Ticket>,
        remoteData: List<Ticket>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        withContext(dispatcher) {
            try {
                // For each ticket in remoteData, check if it exists in localData
                for (ticket in remoteData) {
                    val ticketExistsLocally = localData.any { it.id == ticket.id }
                    if (!ticketExistsLocally) {
                        viewModel.insertTicket(ticket)
                    }
                }

                showSyncResult(isSuccess = true)
                callByFilter()
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing remote data to local: ${e.message}", e)
                showSyncResult(isSuccess = false)
            }
        }
    }

    private fun showFilterDialog() {
        val dialogFragment = FilterDialogFragment(
            startDate = viewModel.startDate,
            endDate = viewModel.endDate,
            licenseNumber = viewModel.licenseNumber,
            driverName = viewModel.driverName,
            listener = object : FilterDialogFragment.OnApplyListener {
                override fun onApply(startDate: String?, endDate: String?, licenseNumber: String?, driverName: String?) {
                    viewModel.startDate = startDate
                    viewModel.endDate = endDate
                    viewModel.licenseNumber = licenseNumber
                    viewModel.driverName = driverName

                    binding.searchView.setQuery("", false)
                    callByFilter()
                }
            }
        )

        dialogFragment.show(supportFragmentManager, "filter_dialog")
    }

    private fun callByFilter() {
        CoroutineScope(Dispatchers.IO).launch {
            when {
                !viewModel.startDate.isNullOrBlank() && !viewModel.endDate.isNullOrBlank() && !viewModel.licenseNumber.isNullOrBlank() && !viewModel.driverName.isNullOrBlank() -> {
                    viewModel.getTicketsByDateRangeAndLicenseAndDriver(viewModel.startDate!!, viewModel.endDate!!, viewModel.licenseNumber!!, viewModel.driverName!!)
                }
                !viewModel.startDate.isNullOrBlank() && !viewModel.endDate.isNullOrBlank() && !viewModel.licenseNumber.isNullOrBlank() -> {
                    viewModel.getTicketsByDateRangeAndLicense(viewModel.startDate!!, viewModel.endDate!!, viewModel.licenseNumber!!)
                }
                !viewModel.startDate.isNullOrBlank() && !viewModel.endDate.isNullOrBlank() && !viewModel.driverName.isNullOrBlank() -> {
                    viewModel.getTicketsByDateRangeAndDriver(viewModel.startDate!!, viewModel.endDate!!, viewModel.driverName!!)
                }
                !viewModel.licenseNumber.isNullOrBlank() && !viewModel.driverName.isNullOrBlank() -> {
                    viewModel.getTicketsByLicenseAndDriver(viewModel.licenseNumber!!, viewModel.driverName!!)
                }
                !viewModel.licenseNumber.isNullOrBlank() -> {
                    viewModel.getTicketsByLicenseNumber(viewModel.licenseNumber!!)
                }
                !viewModel.driverName.isNullOrBlank() -> {
                    viewModel.getTicketsByDriverName(viewModel.driverName!!)
                }
                !viewModel.startDate.isNullOrBlank() && !viewModel.endDate.isNullOrBlank() -> {
                    viewModel.getTicketsByDateRange(viewModel.startDate!!, viewModel.endDate!!)
                }
                else -> {
                    viewModel.getAllTickets()
                }
            }
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val resultValue = data?.getStringExtra("toast_message")
            Toast.makeText(binding.root.context, resultValue, Toast.LENGTH_SHORT).show()
            binding.searchView.setQuery("", false)
            callByFilter()
            syncData()
        }
    }

    private suspend fun fetchRemoteData(): List<Ticket> {
        return suspendCoroutine { continuation ->
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tickets = mutableListOf<Ticket>()
                    for (childSnapshot in snapshot.children) {
                        val ticket = childSnapshot.getValue(Ticket::class.java)
                        ticket?.let { tickets.add(it) }
                    }
                    continuation.resume(tickets)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    private fun showSyncProgreess(){
        with(binding){
            customActionBar.progressBar.visibility = View.VISIBLE
            customActionBar.tvSync.visibility = View.VISIBLE
            customActionBar.tvSyncStatus.visibility = View.GONE
            customActionBar.ivSyncStatus.visibility = View.GONE
        }
    }

    private fun showSyncResult(isSuccess: Boolean){
        with(binding){
            CoroutineScope(Dispatchers.Main).launch {
                customActionBar.progressBar.visibility = View.GONE
                customActionBar.tvSync.visibility = View.GONE
                customActionBar.tvSyncStatus.visibility = View.VISIBLE
                customActionBar.ivSyncStatus.visibility = View.VISIBLE

                if (isSuccess){
                    customActionBar.ivSyncStatus.setImageResource(R.drawable.green_check_circle_outline_32)
                    customActionBar.tvSyncStatus.text = getString(R.string.sync_success)
                } else {
                    customActionBar.ivSyncStatus.setImageResource(R.drawable.red_error_outline_32)
                    customActionBar.tvSyncStatus.text = getString(R.string.sync_failed)
                }
            }
        }
    }
}