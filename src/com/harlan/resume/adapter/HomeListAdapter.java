package com.harlan.resume.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.harlan.resume.activity.R;

public class HomeListAdapter extends BaseAdapter{
	
	private static final String TAG ="HomeListAdapter";
	
	private Context mContext;
	
	 private List<HomeIndexInfo> mDataList;
	 
	 private LayoutInflater mInflater = null;
	
	 int[] imgIds = new int[]{
			 R.drawable.img_baseinfo,R.drawable.img_edu,R.drawable.img_certificate,
			 R.drawable.img_rtconn,R.drawable.img_blog,
			 R.drawable.img_workintent,R.drawable.img_workability,R.drawable.img_proexp,
			 R.drawable.img_salary,R.drawable.img_otherability,
			 R.drawable.img_update,R.drawable.img_history,R.drawable.img_copyright
	 };
	 
	public HomeListAdapter(Context context){
		super();
		Log.d(TAG, " HomeListAdapter(Context context)");
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mDataList = new ArrayList<HomeListAdapter.HomeIndexInfo>();
		String[] indexlist = mContext.getResources().getStringArray(R.array.home_list_index);
		String[] contextlist = mContext.getResources().getStringArray(R.array.home_list_context);
		for (int i = 0;i<contextlist.length;i++){
//			Log.d(TAG, contextlist[i]);
			HomeIndexInfo homeIndexInfo = new HomeIndexInfo();
			homeIndexInfo.imgId=imgIds[i];
			homeIndexInfo.name=contextlist[i];
			if(i%5==0){
			homeIndexInfo.title=indexlist[i/5];
			Log.d(TAG,homeIndexInfo.title);
			}else{
			homeIndexInfo.title = "000";
			}
			mDataList.add(homeIndexInfo);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		HomeIndexInfo homeIndexInfo = new HomeIndexInfo();
		homeIndexInfo = mDataList.get(position);
		return homeIndexInfo;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null)
        {
//        	Log.d(TAG, "convertView == null");
            convertView = mInflater.inflate(R.layout.activity_home_listitem, null);
            holder = new ViewHolder();
            holder.icon = (ImageView)convertView.findViewById(R.id.home_listitem_img);
            holder.name = (TextView)convertView.findViewById(R.id.home_listitem_txt);
            holder.title = (TextView)convertView.findViewById(R.id.home_listitem_title);
            holder.img = (ImageView)convertView.findViewById(R.id.home_listitem_line);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        
        HomeIndexInfo item = (HomeIndexInfo)getItem(position);
        if (item != null)
        {
        	if (!item.title.equals("000")){
        		holder.title.setText(item.title);
        		holder.title.setVisibility(View.VISIBLE);
        		holder.img.setVisibility(View.VISIBLE);
        		 Log.d(TAG, item.title);
        	}else{
        		holder.title.setVisibility(View.GONE);
        		holder.img.setVisibility(View.GONE);
        	}
        	holder.icon.setImageResource(item.imgId);
            holder.name.setText(item.name);
        }
        return convertView;
	}
	
	static class ViewHolder
    {
        public ImageView icon;
        
        public TextView name;
        
        public TextView title;
        
        public ImageView img;
    }
	
	class HomeIndexInfo
	{
		public String title;
		
		public String name;
		
		public int imgId;
	}

}
