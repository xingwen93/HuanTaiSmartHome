package huantai.smarthome.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xmcamera.core.model.XmAccount;
import com.xmcamera.core.model.XmDevice;
import com.xmcamera.core.model.XmErrInfo;
import com.xmcamera.core.sys.XmSystem;
import com.xmcamera.core.sysInterface.IXmSystem;
import com.xmcamera.core.sysInterface.OnXmListener;
import com.xmcamera.core.sysInterface.OnXmMgrConnectStateChangeListener;
import com.xmcamera.core.sysInterface.OnXmSimpleListener;

import java.util.ArrayList;
import java.util.List;

import huantai.smarthome.initial.R;
import huantai.smarthome.utils.ActivityManager;
import huantai.smarthome.video.AddDeviceUserEnsure;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class VideoFragment extends Fragment {

    private IXmSystem xmSystem;
    private XmAccount account;
    private ListView listView;
    private MyAdapter adapter;
    private List<XmDevice> mlist;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(getContext(), "监控视频加载成功！", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), "监控视频加载失败！", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    private View view;
    private ImageView iv_vedio_add;
    private ImageView iv_video_back;

    public VideoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);
        init();
        initview();
        initdata();
        setEvent();
//        loadVideo();
        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        init();
//        initview();
//        initdata();
//        setEvent();
//    }

    ActivityManager manager;

    private void init() {
        xmSystem = XmSystem.getInstance();
//        IntentFilter intentFilter = new IntentFilter(ConstAction.sendvideoaction);
//        getContext().registerReceiver(videoReceiver,intentFilter);
//        System.out.print(1);
        account = MainActivity.account;
//        account = (XmAccount) getActivity().getIntent().getExtras().getSerializable("user");
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            account = (XmAccount) bundle.getSerializable("user");
//        }
        xmSystem.registerOnMgrConnectChangeListener(onXmMgrConnectStateChangeListener);
        loginMgr();
        manager = ActivityManager.getInstance();
        manager.addActivity(getActivity());
    }

//    private BroadcastReceiver videoReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            account = (XmAccount) intent.getExtras().getSerializable("user");
//        }
//    };

    private void loginMgr() {
        xmSystem.xmMgrSignin(new OnXmSimpleListener() {
            @Override
            public void onErr(XmErrInfo info) {
//                Log.v("AAAAA", "MgrSignin fail!");
            }

            @Override
            public void onSuc() {
//                Log.v("AAAAA", "MgrSignin suc!");
            }
        });
    }

    OnXmMgrConnectStateChangeListener onXmMgrConnectStateChangeListener = new OnXmMgrConnectStateChangeListener() {
        @Override
        public void onChange(boolean connectState) {
            Log.v("AAAAA", "OnXmMgrConnectStateChangeListener onChange is " + connectState);
        }
    };

    private void initview() {
        iv_vedio_add = (ImageView) view.findViewById(R.id.iv_vedio_add);
        iv_video_back = (ImageView) view.findViewById(R.id.iv_video_back);
        listView = (ListView) view.findViewById(R.id.lv_vedio_list);
        mlist = new ArrayList<XmDevice>();
        adapter = new MyAdapter(getContext());
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(itemListener);
    }

    private void initdata() {
//        xmSystem = XmSystem.getInstance();
//        if(!getIntent().getExtras().getBoolean("isDemo")) {
//            account = (XmAccount) getIntent().getExtras().getSerializable("username");
//        }
        getDevices();
    }

    private void setEvent() {
        iv_vedio_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice();
            }
        });
        iv_video_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getDevices() {
        Log.v("AAAAA", "getDevices");
        xmSystem.xmGetDeviceList(new OnXmListener<List<XmDevice>>() {
            @Override
            public void onErr(XmErrInfo info) {
                Log.v("AAAAA", "get devices fail");
            }

            @Override
            public void onSuc(List<XmDevice> info) {
                Log.v("AAAAA", "getDevices  onSuc");
                mlist = info;
                adapter.notifyDataSetChanged();
            }
        });
    }

    //增加设备
    private void addDevice() {
        Intent in = new Intent(getActivity(), AddDeviceUserEnsure.class);
        in.putExtra("isDemo", account.isDemo());
        if (!account.isDemo())
            in.putExtra("username", account.getmUsername());
        startActivity(in);
    }


    //设备选择事件
//    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            int cameraId = mlist.get(position).getmCameraId();
//            Intent in = new Intent(DeviceslistActivity.this, PlayActivity.class);
//            in.putExtra("cameraId",cameraId);
//            startActivityForResult(in,100);
//        }
//    };

    class MyAdapter extends BaseAdapter {

        Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoler holer = null;
            if (convertView == null) {
                holer = new ViewHoler();
                convertView = LayoutInflater.from(context).inflate(R.layout.vedio_list_item, null);
                holer.tv = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holer);
            } else {
                holer = (ViewHoler) convertView.getTag();
            }
            holer.tv.setText(mlist.get(position).getmName());
            return convertView;
        }

        class ViewHoler {
            TextView tv;
        }
    }


//    IXmSystem xmSystem;
//    private void initVideo() {
//        //初始化爱小屏sdk
//        xmSystem = XmSystem.getInstance();
//        xmSystem.xmInit(getContext(), "CN", new OnXmSimpleListener() {
//            @Override
//            public void onErr(XmErrInfo info) {
//                Log.v("AAAAA", "init Fail");
//            }
//
//            @Override
//            public void onSuc() {
//                Log.v("AAAAA", "init Suc");
//            }
//        });
//    }
//    private void loadVideo() {
//        ((MainActivity) getActivity()).showLoadingDialog();
//        try {
//            ((MainActivity) getActivity()).xmSystem.xmLogin("13135367953",
//                    "chen162858", new OnXmListener<XmAccount>() {
//                        @Override
//                        public void onSuc(XmAccount outinfo) {
//                            ((MainActivity) getActivity()).closeLoadingDialog();
//                            handler.sendEmptyMessage(1);
////                            ((MainActivity) getActivity()).sp.setUsername("13135367953");
//                            System.out.print(1);
//                            ((MainActivity) getActivity()).loginSuc(outinfo);
//                        }
//
//                        @Override
//                        public void onErr(XmErrInfo info) {
//                            ((MainActivity) getActivity()).closeLoadingDialog();
//                            handler.sendEmptyMessage(2);
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//            ((MainActivity) getActivity()).closeLoadingDialog();
//            handler.sendEmptyMessage(2);
//        } finally {
//
//        }
//    }
}
