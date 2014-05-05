package com.harlan.resume.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.harlan.resume.activity.R;

public class FlingGalleryAdapter extends BaseAdapter{
	
//	private final int color_red = Color.argb(100, 200, 0, 0);
//	private final int color_green = Color.argb(100, 0, 200, 0);
//	private final int color_blue = Color.argb(100, 0, 0, 200);
//	private final int color_yellow = Color.argb(100, 200, 200, 0);
//	private final int color_purple = Color.argb(100, 200, 0, 200);
//	private final int[] mColorArray = {color_red, color_green, color_blue, color_yellow, color_purple};

    private final String[] mIndexArray;
    private final String[] mNameArray;
    private final String[] mStationArray;
    private final String[] mInfoArray;
    private final String[] mNumArray;
    private final String[] mTaskArray;

    private static final String TAG ="FlingGalleryAdapter";
	
	private Context mContext;
	
	 private List<ProjectExpInfo> mDataList;
	 
	 private LayoutInflater mInflater = null;
    
	 public FlingGalleryAdapter(Context context){
		 super();
		 mContext = context;
		 Resources res = mContext.getResources();
		 mIndexArray = res.getStringArray(R.array.proexp_index_list);
		 mNameArray = res.getStringArray(R.array.proexp_name_list);
		 mStationArray = res.getStringArray(R.array.proexp_station_list);
		 mInfoArray = res.getStringArray(R.array.proexp_info_list);
		 mNumArray = res.getStringArray(R.array.proexp_number_list);
		 mTaskArray = res.getStringArray(R.array.proexp_task_list);
		 mInflater = LayoutInflater.from(mContext);
		 mDataList = new ArrayList<ProjectExpInfo>();
			for (int i = 0;i<mIndexArray.length;i++){
				ProjectExpInfo projectExpInfo = new ProjectExpInfo();
				projectExpInfo.projectIndex = mIndexArray[i];
				projectExpInfo.projectName = mNameArray[i];
				projectExpInfo.projectStation = mStationArray[i];
				projectExpInfo.projectInfo = mInfoArray[i];
				projectExpInfo.projectNum = mNumArray[i];
				projectExpInfo.projectTask = mTaskArray[i];
				mDataList.add(projectExpInfo);
				Log.e(TAG,projectExpInfo.projectIndex);
			}
	 }
	 
	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		ProjectExpInfo projectExpInfo = new ProjectExpInfo();
		projectExpInfo = mDataList.get(position);
		return projectExpInfo;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		convertView = mInflater.inflate(R.layout.activity_proexp_galleryitem,
				null);
		holder = new ViewHolder();
		holder.index = (TextView) convertView
				.findViewById(R.id.proexp_txt_index);
		holder.name = (TextView) convertView
				.findViewById(R.id.proexp_txt_project_name);
		holder.station = (TextView) convertView
				.findViewById(R.id.proexp_txt_work_station);
		holder.info = (TextView) convertView
				.findViewById(R.id.proexp_txt_work_info);
		holder.number = (TextView)convertView
				.findViewById(R.id.proexp_txt_work_number);
		holder.task = (TextView) convertView
				.findViewById(R.id.proexp_txt_work_task);
		convertView.setTag(holder);

		ProjectExpInfo item = (ProjectExpInfo) getItem(position);
        if (item != null)
        {
        	Log.e(TAG, item.projectIndex);
        	  holder.index.setText(item.projectIndex);
              holder.name.setText(item.projectName);
              holder.station.setText(item.projectStation);
              holder.info.setText(item.projectInfo);
              holder.number.setText(item.projectNum);
              holder.task.setText(item.projectTask);
        }
        return convertView;
	}
	
	
	static class ViewHolder
    {
		public TextView index;
		
        public TextView name;
        
        public TextView station;
        
        public TextView info;
        
        public TextView number;
        
        public TextView task;
    }
	
	class ProjectExpInfo
	{
		public String projectIndex;
		
		public String projectName;
		
		public String projectStation;
		
		public String projectInfo;
		
		public String projectNum;
		
		public String projectTask;
	}

}
