package me.tiptap.tiptap.diaries

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.common.view.DatePickerDialogFragment
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.databinding.FragmentDiariesBinding
import me.tiptap.tiptap.diarydetail.DiaryDetailActivity
import java.util.*

class DiariesFragment : Fragment() {

    private lateinit var binding: FragmentDiariesBinding

    private val adapter = DiariesAdapter()

    private val bus = RxBus.getInstance()
    private val disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var datePickerDialog: DatePickerDialogFragment


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diaries, container, false)
        binding.fragment = this

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
        initRecyclerView()
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbarDiaries?.toolbarDiaries)

            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
            }
        }
    }


    private fun initRecyclerView() {
        binding.recyclerDiaries.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@DiariesFragment.context)
            adapter = this@DiariesFragment.adapter.apply {

                //Dummy data
                for (i in 1..15) {
                    addItem(Diary(i, Date(), "내용 $i", "서울대입구 $i 번 출구 앞", Uri.parse("none")))
                }
                binding.date = getItem(0).date

                disposables.addAll(
                        clickSubject.subscribe {
                            //Go to detail page if actionMode is not running.
                            if (this.isCheckboxAvailable.get() == false) {
                                bus.takeBus(it)
                                startActivity(Intent(this@DiariesFragment.activity, DiaryDetailActivity::class.java))
                            }
                        },
                        checkSubject.subscribe {
                            onCheckedChangeEventPublished(it)
                        })
            }
        }
    }


    fun onDateFindButtonClick() {
        if (!::datePickerDialog.isInitialized) {
            datePickerDialog = DatePickerDialogFragment()
        }

        activity?.let {
            datePickerDialog.show(it.supportFragmentManager, it.getString(R.string.app_name))
        }
    }

    /**
     * when click delete Icon
     */
    fun onDeleteMenuItemClick() {
        //checkbox mode on/off
        adapter.isCheckboxAvailable.get()?.let {
            setBottomDialogVisibility(!it) //change bottom dialog visibility
            adapter.isCheckboxAvailable.set(!it)
        }
    }

    private fun setBottomDialogVisibility(value : Boolean) {
        if (value) binding.layoutBotDialog?.constBotDial?.visibility = View.VISIBLE
        else binding.layoutBotDialog?.constBotDial?.visibility = View.GONE
    }


    /**
     *  When click bottom dialog button
     */
    fun onBottomDialogButtonClick(view : View) {
        when (view.id) {
            R.id.text_bot_dial_cancel -> {
                adapter.clearChecked(false)
            }
            R.id.text_bot_dial_delete -> {
                adapter.deleteItems()
            }
        }
        adapter.isCheckboxAvailable.set(false)
        setBottomDialogVisibility(false)
    }


    override fun onDestroy() {
        super.onDestroy()

        //Dispose subjects to prevent memory leak.
        disposables.dispose()
    }
}