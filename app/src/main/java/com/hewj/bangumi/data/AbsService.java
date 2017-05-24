package com.hewj.bangumi.data;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hewj.bangumi.entity.BangumiEntity;
import com.hewj.bangumi.entity.BangumiEpisodeEntity;
import com.hewj.bangumi.entity.BangumiGroupEntity;
import com.hewj.bangumi.entity.BangumiInfoEntity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by hewj on 2017/5/10.
 * 从服务器获取数据
 */

public class AbsService {

    private static final String FORUM_SERVER_URL = "http://baike.baidu.com/api/openapi/";//基础URL
    private AbsApi mAbsApi;
    private volatile static AbsService singleton;
    final static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .serializeNulls()
            .create();

    public AbsService() {
//公共参数
        Interceptor addQueryParameterInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request request;
                String method = originalRequest.method();
                Headers headers = originalRequest.headers();
                HttpUrl modifiedUrl = originalRequest.url().newBuilder()
                        // Provide your custom parameter here
                        .addQueryParameter("scope", "103")
                        .build();
                request = originalRequest.newBuilder().url(modifiedUrl).build();
                return chain.proceed(request);
            }
        };

//打印日志
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(addQueryParameterInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FORUM_SERVER_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mAbsApi = retrofit.create(AbsApi.class);
    }
    public AbsApi getApi() {

        return mAbsApi;
    }

    /**
    * 当数据来源是服务器的时候用API的形式访问
    * */
    public interface AbsApi {
//        @GET("BaikeLemmaCardApi")
//        Observable<jsonOut>
//        getBaidu(@Query("bk_key") String key);//获取新闻
    }

    /**
     * 爬虫访问网络，构造周一到周末的新番列表
     * */
    public Observable<AbsReturn<ArrayList<ArrayList<BangumiEntity>>>> getBangumiList(){
        return Observable.create(f->{
            ArrayList<ArrayList<BangumiEntity>> arrayList=new ArrayList<ArrayList<BangumiEntity>>();
            AbsReturn<ArrayList<ArrayList<BangumiEntity>>> bangumiEntityAbsReturn=new AbsReturn<ArrayList<ArrayList<BangumiEntity>>>();
            bangumiEntityAbsReturn.setCode(1);
            bangumiEntityAbsReturn.setMessage("success");
            bangumiEntityAbsReturn.setData(arrayList);

            String url = "http://www.fengchedm.com/";
            Connection conn = Jsoup.connect(url);
            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements noteList = doc.select("div.tists");
            Elements ul = noteList.select("ul");
            for (Element ulElement : ul) {
                Elements li=ulElement.select("li");
                ArrayList<BangumiEntity> bangumiEntities=new ArrayList<BangumiEntity>();
                for (Element liElement : li) {
                    BangumiEntity bangumiEntity=new BangumiEntity();
                    bangumiEntity.setNumber(liElement.select("a").first().text());
                    bangumiEntity.setTitle(liElement.select("a").last().text());
                    bangumiEntity.setUrl(liElement.select("a").last().attr("abs:href"));
                    bangumiEntities.add(bangumiEntity);
                }
                arrayList.add(bangumiEntities);
            }
            f.onNext(bangumiEntityAbsReturn);
        });
    }

    /**
     * 爬虫访问网络，某个番剧的详细信息
     * */
    public Observable<AbsReturn<BangumiInfoEntity>> getItemInfo(String url){
        return Observable.create(f->{
            BangumiInfoEntity bangumiInfoEntity =new BangumiInfoEntity();
            AbsReturn<BangumiInfoEntity> a=new AbsReturn<BangumiInfoEntity>();
            a.setCode(1);
            a.setMessage("success");
            a.setData(bangumiInfoEntity);

            Connection conn = Jsoup.connect(url);
            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            bangumiInfoEntity.setName(doc.select("div.spay").select("a").text());
            bangumiInfoEntity.setCover(doc.select("div.tpic.l").select("img").attr("src"));
            Elements noteList = doc.select("div.alex").select("span");
            bangumiInfoEntity.setAll(noteList.get(0).text());
            bangumiInfoEntity.setState(noteList.get(1).text());
            bangumiInfoEntity.setAutor(noteList.get(2).text());
            bangumiInfoEntity.setVersion(noteList.get(3).text());
            bangumiInfoEntity.setType(noteList.get(4).text());


            f.onNext(a);
        });
    }

    /**
     * 爬虫访问网络，构造某个新番的具体集数
     * */
    public Observable<AbsReturn<BangumiGroupEntity>> getItemList(String url){
        return Observable.create(f->{
            BangumiGroupEntity t=new BangumiGroupEntity();
            AbsReturn<BangumiGroupEntity> a=new AbsReturn<BangumiGroupEntity>();
            a.setCode(1);
            a.setMessage("success");
            a.setData(t);

            ArrayList<String> title=new ArrayList();
            ArrayList<ArrayList<BangumiEpisodeEntity>> child=new ArrayList<ArrayList<BangumiEpisodeEntity>>();
            Connection conn = Jsoup.connect(url);
            // 修改http包中的header,伪装成浏览器进行抓取
            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements noteList = doc.select("div.tabs");
            Elements ul = noteList.select("ul.menu0");
            Elements div = noteList.select("div.main0");
            for (Element ulElement : ul) {
                title.add(ulElement.select("li.on").text());
            }
            for(Element divElement:div){
                Elements li=divElement.select("li");
                ArrayList<BangumiEpisodeEntity> bangumiEpisodeEntities=new ArrayList<BangumiEpisodeEntity>();
                for (Element liElement : li) {
                    BangumiEpisodeEntity bangumiEpisodeEntity=new BangumiEpisodeEntity();
                    bangumiEpisodeEntity.setNum(liElement.select("a").text());
                    bangumiEpisodeEntity.setUrl(liElement.select("a").attr("abs:href"));
                    bangumiEpisodeEntities.add(bangumiEpisodeEntity);
                }
                child.add(bangumiEpisodeEntities);
            }
            t.setGroup(title);
            t.setChild(child);
            f.onNext(a);
        });
    }


    public static AbsService getInstance() {
        if (singleton == null) {
            synchronized (AbsService.class) {
                if (singleton == null) {
                    singleton = new AbsService();
                }
            }
        }
        return singleton;
    }

    public static AbsApi getService(){
        return getInstance().mAbsApi;
    }
}
