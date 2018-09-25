package me.tiptap.tiptap.scratch

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.data.ShareResponse
import me.tiptap.tiptap.databinding.FragmentScratchBinding


class ScratchFragment : Fragment() {

    private lateinit var binding: FragmentScratchBinding

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val disposables = CompositeDisposable()

    private val adapter = SharingAdapter()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scratch, container, false)

        initBind()

        setShareMainLayoutSize() // Change share main container's height

        return binding.root
    }


    private fun initBind() {
        binding.scratch.setRevealListener(object : ScratchCard.IRevealListener {
            override fun onRevealPercentChangedListener(stv: ScratchCard, percent: Float) {
                if (percent <= 0.2f) {
                    if (!stv.isRevealed) {
                        stv.mRevealPercent = 1.0f
                    }
                }
            }

            override fun onRevealed(tv: ScratchCard) {
                tv.isRevealed = true
                tv.fadeOutAnimation(binding.scratch, 300)

                getShareDiary() //get Share diary if scratch is revealed.
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
    }

    /**
     * Change Share main layout container's height to screen size.
     */
    private fun setShareMainLayoutSize() {
        val point = Point()

        activity?.windowManager?.defaultDisplay?.getSize(point)

        binding.containerScratchMain.layoutParams.height = point.y //change height
    }


    private fun getShareDiary() {
        Log.d("Share", "get Share diary")
        disposables.add(
                service.shareDiaries(TipTapApplication.getAccessToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableObserver<ShareResponse>() {
                            override fun onComplete() {
                                binding.layoutScratchMain?.textScratchMainNum?.text = getString(R.string.count_tiptap, adapter.itemCount)
                            }

                            override fun onNext(t: ShareResponse) {
                                if (t.code == "1000") {
                                    t.data?.let {
                                        adapter.addItems(it.shareDiaries)
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })
        )
    }


    private fun initRecyclerView() {
        binding.recyclerSharing.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@ScratchFragment.context)
            adapter = this@ScratchFragment.adapter.apply {
            }
        }
    }

    override fun onStop() {
        super.onStop()

        disposables.dispose()
    }
}