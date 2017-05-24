/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.hewj.library.view.list;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.hewj.library.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 除了继承IXListViewListener这个监听外，页面刷新后必须调用onLoad设置状态，否则下次无法刷新
 */
public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private XListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	
	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.

	String mLastUpdateTimeString = "";

	
	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);
 
		// init header view
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		// init footer view
		mFooterView = new XListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}
	
	public void setPullEnable(boolean enable)
	{
		setPullRefreshEnable(enable);
		setPullLoadEnable(enable);
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		} else {
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mLastUpdateTimeString = time;
		//mHeaderTimeView.setText(formattimeString);
	}
	
	public void calculateTime()
	{
		mHeaderTimeView.setText(topTimeFormat(mLastUpdateTimeString));
	}  

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // ???浜???扮?????存?绠?ご
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	public void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}
	
	
	
	

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

//		setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}
	
	boolean mbMovingByOther = false;
	public void scrollDistance(int distance,boolean moveByOther) {
		mbMovingByOther = moveByOther;
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, 0, 0, distance,1);
		invalidate();
	}
	
	
	public void manualRefresh()
	{
		try {
//			int mScroolPosition = getChildAt(0).getTop();
			if (mPullRefreshing || mbScrooling) {
				return;
			}
			calculateTime();
			mPullRefreshing = true;
			mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
			if (mListViewListener != null) {
				mListViewListener.onRefresh();
			}
			setSelection(0); 
			mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
			mScrollBack = SCROLLBACK_HEADER;
			mScroller.startScroll(0, 0, 0, mHeaderViewHeight,
					SCROLL_DURATION);
			invalidate();

//			int height = mHeaderView.getVisiableHeight();
//			if (height == 0) // not visible.
//				return;
//			// refreshing and header isn't shown fully. do nothing.
//			if (mPullRefreshing && height <= mHeaderViewHeight) {
//				return;
//			}
//			int finalHeight = 0; // default: scroll back to dismiss header.
//			// is refreshing, just scroll back to show all the header.
//			if (mPullRefreshing && height > mHeaderViewHeight) {
//				finalHeight = mHeaderViewHeight;
//			}
//			mScrollBack = SCROLLBACK_HEADER;
//			mScroller.startScroll(0, height, 0, finalHeight - height,
//					SCROLL_DURATION);
//			// trigger computeScroll
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean mbFirstDagDown = false;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			
			mbFirstDagDown = true;
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0) && mEnablePullRefresh) {
				// the first item is showing, header has shown or pull down.
				if (mbFirstDagDown) {
					
					mbFirstDagDown = false;
					if(mListViewListener != null)
					{
						mListViewListener.onStartPullDown();
					}
				}
				
				
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} 
//			else if (getLastVisiblePosition() > mTotalItemCount - 2
//					) {//&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)
//				// last item, already pulled up or want to pull up.
//				//updateFooterHeight(-deltaY / OFFSET_RADIO);
//				
//				Debugs.e("star", "ddddddddddddddd");
//				if (mEnablePullLoad
//						) {
//					startLoadMore();
//					Debugs.e("star", "ddddddddddddddd111");
//				}
//				resetFooterHeight();
//			}
			
			
			break;
		default:
			mbFirstDagDown = true;
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight && !mPullRefreshing) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
					
					
				}
				resetHeaderHeight();
			}
//			else if (getLastVisiblePosition() > mTotalItemCount - 2) {
//				// invoke load more.
//				if (mEnablePullLoad
//						) {//&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
//					startLoadMore();
//				}
//				resetFooterHeight();
//			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	public static String topTimeFormat(String timeString) {
		if (TextUtils.isEmpty(timeString)) {
			return "刚刚";
		}
		timeString = timeString.replace("T", " ");
		SimpleDateFormat formattera = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String currentString = formattera.format(curDate);

		Calendar today = Calendar.getInstance();
		Calendar target = Calendar.getInstance();

		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			today.setTime(df.parse(currentString));
			target.setTime(df.parse(timeString));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return timeString;
		}

		long col = (today.getTimeInMillis() - target.getTimeInMillis());
		double colTime = col * 1.0 / 1000;// s
		if (colTime / (60) <= 1) {
			// 一分钟之内
			timeString = "刚刚";
		} else if (colTime / 60 > 1 && colTime / 3600 <= 1) {
			// 一分钟到一小时
			timeString = (long) colTime / 60 + " 分钟前";
		} else if (colTime / 3600 > 1 && colTime / 7200 <= 1) {
			// 一小时到2小时
			timeString = (long) colTime / 3600 + " 小时前";
		} else if (colTime / 7200 > 1 && colTime / 86400 <= 1
				&& isToday(timeString)) {
			// 2小时到一天
			SimpleDateFormat formatter1 = new SimpleDateFormat("今天 HH:mm");
			Date curDate1 = new Date(target.getTimeInMillis());// 获取当前时间
			timeString = formatter1.format(curDate1);
		} else {
			SimpleDateFormat formatter1 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			Date curDate1 = new Date(target.getTimeInMillis());// 获取当前时间
			timeString = formatter1.format(curDate1);
		}
		return timeString;
	}
	
	public static boolean isToday(String timeString) {
		if (!TextUtils.isEmpty(timeString)) {
			if (timeString.length() > 16) {
				timeString = timeString.substring(0, 16);
			}
		}
		SimpleDateFormat formattera = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String currentString = formattera.format(curDate);

		Calendar today = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		Calendar tempCalendar = Calendar.getInstance();
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			today.setTime(df.parse(currentString));
			today.set(Calendar.HOUR_OF_DAY, 9);
			// today.set(Calendar.HOUR, 1);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);

			tempCalendar.setTime(df.parse(timeString));
			target.setTime(df.parse(timeString));
			target.set(Calendar.HOUR_OF_DAY, 9);
			// target.set(Calendar.HOUR, 1);
			target.set(Calendar.MINUTE, 0);
			target.set(Calendar.SECOND, 0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		long colTime = today.getTimeInMillis() - target.getTimeInMillis();

		// String lastaaaa = formattera.format(target.getTime());
		// String currentaaaa = formattera.format(today.getTime());
		// Debugs.e("star","last:"+lastaaaa+";current:"+currentaaaa+";col:"+colTime);

		boolean bToday = false;
		boolean bYestoday = false;
		bToday = (colTime == 0);
		bYestoday = (colTime / (24 * 60 * 60 * 1000)) == 1;
		return bToday;
	}
	/*时间规则B
	  	1分钟内发布的微博，标注为“刚刚”。
		若发布时间在1小时，内则显示XX分钟前；
		若发布时间超过1小时，则显示具体时间：今天 10:00
		若发布时间在昨天，则显示：昨天 10:00
		若发布时间在前天，则显示：前天 10:00
		若发布时间超过3天并且在今年，则显示：06.01 10:00
		若发布时间超过今年，则显示：2013.06.01 10:00
	 */
	//传入时间格式为yyyy/MM/dd HH:mm:ss
	public static String formatTimePlanB(String timeString){
		if (TextUtils.isEmpty(timeString)) {
			return "刚刚";
		}
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		long ms  = 1000*(now.get(Calendar.HOUR_OF_DAY)*3600+now.get(Calendar.MINUTE)*60+now.get(Calendar.SECOND))+now.get(Calendar.MILLISECOND);//毫秒数
        long ms_now = now.getTimeInMillis();
		Calendar target = Calendar.getInstance();

		try {
			target.setTime(sourceFormat.parse(timeString));
		} catch (Exception e) {
			return timeString;
		}

		long col = (now.getTimeInMillis() - target.getTimeInMillis());
		double colTime = col * 1.0 / 1000;// s
		if ((colTime / 60)>=0&&(colTime / 60) <= 1) {// 一分钟之内
			timeString = "刚刚";
		} else if (colTime / 60 > 1 && colTime / 3600 <= 1) {// 一分钟到一小时		
			timeString = (long) colTime / 60 + "分钟前";
		} else if (colTime / 3600 > 1 &&target.getTimeInMillis()>=ms_now-ms) {//今天
			SimpleDateFormat formatter1 = new SimpleDateFormat("今天  HH:mm");
			timeString = formatter1.format(target.getTime());
		}
		else if(target.getTimeInMillis()<ms_now-ms&&target.getTimeInMillis()>=ms_now-ms-24*3600*1000){//昨天
			SimpleDateFormat formatter1 = new SimpleDateFormat("昨天  HH:mm");
			timeString = formatter1.format(target.getTime());
		}
		else if(target.getTimeInMillis()<ms_now-ms-24*3600*1000&&target.getTimeInMillis()>=ms_now-ms-24*3600*1000*2){//前天
			SimpleDateFormat formatter1 = new SimpleDateFormat("前天  HH:mm");
			timeString = formatter1.format(target.getTime());
		}
		else if(target.getTimeInMillis()<ms_now-ms-24*3600*1000*2&&now.get(Calendar.YEAR)==target.get(Calendar.YEAR)){//今年
			SimpleDateFormat formatter1 = new SimpleDateFormat("MM.dd  HH:mm");
			timeString = formatter1.format(target.getTime());
		}
		else {
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			timeString = formatter1.format(target.getTime());
		}
		return timeString;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}
	
	

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	boolean mbScrooling = false;
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
		mbScrooling = scrollState != OnScrollListener.SCROLL_STATE_IDLE;
		
		if (getLastVisiblePosition() > mTotalItemCount - 2 && !mPullLoading && OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
			if (mEnablePullLoad
					) {//&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
				startLoadMore();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
		
		
		
	}

    /**
     * 刷新后必须被调用，才不会有问题
     * @param bLast
     */
	public void onLoadComplete(boolean bLast) {
		stopRefresh();
		stopLoadMore();
		String currentString = formatTime(System.currentTimeMillis());
		setRefreshTime(currentString);
		if (bLast) {
			setPullLoadEnable(false);
		} else {
			setPullLoadEnable(true);
		}
	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 * @return
	 */
	static public String formatTime(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(time);// 获取当前时间
		return formatter.format(curDate);
	}
	
	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		void onRefresh();

		void onLoadMore();
		
		void onStartPullDown();
	}
}
