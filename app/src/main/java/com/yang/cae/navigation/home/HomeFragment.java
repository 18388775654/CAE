package com.yang.cae.navigation.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mengpeng.mphelper.ToastUtils;
import com.yang.cae.R;
import com.yang.cae.DataDTO;
import com.yang.cae.bean.result.Result;
import com.yang.cae.configure.requestAPI.RequestAPI;
import com.yang.cae.function.search.SearchActivity;
import com.yang.cae.navigation.home.bannerUilt.GlideImageLoader;
import com.yang.cae.show.ShowActivity;
import com.yang.cae.util.jsonUtil.JsonUtil;
import com.yang.cae.util.okHttpUtil.RetrofitUtil;
import com.yang.cae.util.xbasebrowerUtil.XBaseBrowserUtil;
import com.youth.banner.Banner;

import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment{
    private View root;
    private List<Map<String,Object> > messageList;
    private List<DataDTO> dataDTOList;
    private Banner banner;
    private ListView homeItem;
    private List images;
    private List advertiseUrl;
    private RequestAPI requestApi;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        initView();
        initBanner();
        requestApi = RetrofitUtil.getRetrofit()
                .create(RequestAPI.class);
        Call<ResponseBody> call = requestApi.getHomeData();
        getData(call);

        //banner??????
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                //????????????
                XBaseBrowserUtil.startActivity(getContext(),advertiseUrl.get(position).toString());
            }
        });

        homeItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ShowActivity.class);
                intent.putExtra("dataDTO", dataDTOList.get(position));
                startActivity(intent);
            }
        });
        homeItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Call<ResponseBody> addCollect = requestApi.addCollect(dataDTOList.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addCollect.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    String resp = null;
                                    try {
                                        resp = new String(response.body().bytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Result result = JsonUtil.respBodyParse(resp);
                                    ToastUtils.onSuccessShowToast(result.getMessage());
                                } else {
                                    ToastUtils.onErrorShowToast("???????????????");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });

        return root;
    }


    private void initView(){
        banner = root.findViewById(R.id.banner);
        homeItem = root.findViewById(R.id.home_item);
        Resources res = getResources();
        images= Arrays.asList(res.getStringArray(R.array.pic_url));
        advertiseUrl = Arrays.asList(res.getStringArray(R.array.advertise_url));
        }

    private void initBanner(){
        banner.setImageLoader(new GlideImageLoader());
        //??????????????????
        banner.setImages(images);
        //????????????????????????
        banner.setDelayTime(3000);
        //banner?????????????????????????????????????????????
        banner.start();
    }

    private void renderListView(List<DataDTO> dataDTOList){
        messageList = new ArrayList<>();
        int num = 1;
        for (DataDTO dataDTO : dataDTOList) {
            num = num+1;
            //???????????????????????????????????????,??????item??????????????????
            Map<String,Object> item = new HashMap<String,Object>();
            //?????????????????????????????????
            item.put("id", dataDTO.getMessageId());
            item.put("flag", dataDTO.getFlag());
            item.put("name", dataDTO.getMessageName());
            item.put("time", dataDTO.getDate());;
            messageList.add(item);
        }
        //startManagingCursor(cursor);
        String[] from=new String[]{"name","time"};
        int to[]=new int[]{R.id.name,R.id.time};
        SimpleAdapter adapter=new SimpleAdapter(getActivity(),messageList,R.layout.layout_home_list,from,to);
        //???????????????????????????ListView??????????????????????????????????????????????????????ListView???????????????????????????0.
        ListView listView=root.findViewById(R.id.home_item);
        listView.setAdapter(adapter);
    }

    public void getData(Call<ResponseBody> call){
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()){
                        String resp = new String(response.body().bytes());
                        Result result = JsonUtil.respBodyParse(resp);
                        dataDTOList = result.getData().toJavaList(DataDTO.class);
                        renderListView(dataDTOList);
                    }else {
                        ToastUtils.onErrorShowToast("???????????????");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

}
