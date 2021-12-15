package com.edw;

import com.edw.bean.Regions;
import com.edw.config.MyBatisSqlSessionFactory;
import com.edw.mapper.RegionsMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *     com.edw.RegionalScrapping
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 14 Des 2021 12:21
 */
public class RegionalScrapping {

    private static final String BASE_URL = "https://sig.bps.go.id";

    private Logger logger = LogManager.getLogger(RegionalScrapping.class);

    public RegionalScrapping() {
    }

    public static void main(String[] args) throws IOException {
        RegionalScrapping regionalScrapping = new RegionalScrapping();
        regionalScrapping.doScrapping();
    }

    private void doScrapping() throws IOException {
        getKelurahan();
    }

    private List<HashMap> doHttpCall(Request request) throws IOException {
        Call call = new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS).build().newCall(request);
        Response response = call.execute();

        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap> hashMaps = objectMapper.readValue(response.body().string(), List.class);

        response.close();
        return hashMaps;
    }

    private void getProvinsis() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-drop-down-dagri/getwilayah")
                .get()
                .build();

        List<HashMap> hashMaps = doHttpCall(request);

        for (HashMap hashMap : hashMaps) {
            Regions regions = new Regions();
            regions.setParentCode("0");
            regions.setRegionCode(hashMap.get("kode").toString());
            regions.setRegionName(hashMap.get("nama").toString());

            insert(regions);
        }
    }

    private void getKabupatens() throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-drop-down-dagri/getwilayah")
                .get()
                .build();

        List<HashMap> provinsis = doHttpCall(request);

        for (HashMap hashMap : provinsis) {
            String parent = hashMap.get("kode").toString();

            request = new Request.Builder()
                    .url(BASE_URL + "/rest-drop-down-dagri/getwilayah?level=kabupaten&parent="+parent)
                    .get()
                    .build();

            List<HashMap> kabupatens = doHttpCall(request);

            for (HashMap kabupaten : kabupatens) {
                Regions regions = new Regions();
                regions.setParentCode(parent);
                regions.setRegionCode(kabupaten.get("kode").toString());
                regions.setRegionName(kabupaten.get("nama").toString());

                insert(regions);
            }
        }
    }


    private void getKecamatans() throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-drop-down-dagri/getwilayah")
                .get()
                .build();

        List<HashMap> provinsis = doHttpCall(request);

        for (HashMap hashMap : provinsis) {
            String parent = hashMap.get("kode").toString();

            request = new Request.Builder()
                    .url(BASE_URL + "/rest-drop-down-dagri/getwilayah?level=kabupaten&parent="+parent)
                    .get()
                    .build();

            List<HashMap> kabupatens = doHttpCall(request);

            for (HashMap kabupaten : kabupatens) {
                parent = kabupaten.get("kode").toString();

                request = new Request.Builder()
                        .url(BASE_URL + "/rest-drop-down-dagri/getwilayah?level=kecamatan&parent="+parent)
                        .get()
                        .build();

                List<HashMap> kecamatans = doHttpCall(request);
                for (HashMap kecamatan : kecamatans) {
                    Regions regions = new Regions();
                    regions.setParentCode(parent);
                    regions.setRegionCode(kecamatan.get("kode").toString());
                    regions.setRegionName(kecamatan.get("nama").toString());

                    insert(regions);
                }
            }
        }
    }



    private void getKelurahan() throws IOException {

        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-drop-down-dagri/getwilayah")
                .get()
                .build();

        List<HashMap> provinsis = doHttpCall(request);

        for (HashMap hashMap : provinsis) {
            String parent = hashMap.get("kode").toString();

            request = new Request.Builder()
                    .url(BASE_URL + "/rest-drop-down-dagri/getwilayah?level=kabupaten&parent="+parent)
                    .get()
                    .build();

            List<HashMap> kabupatens = doHttpCall(request);

            for (HashMap kabupaten : kabupatens) {
                parent = kabupaten.get("kode").toString();

                request = new Request.Builder()
                        .url(BASE_URL + "/rest-drop-down-dagri/getwilayah?level=kecamatan&parent="+parent)
                        .get()
                        .build();

                List<HashMap> kecamatans = doHttpCall(request);
                for (HashMap kecamatan : kecamatans) {
                    parent = kecamatan.get("kode").toString();

                    request = new Request.Builder()
                            .url(BASE_URL + "/rest-bridging-dagri/getwilayah?level=desa&parent="+parent)
                            .get()
                            .build();

                    List<HashMap> kelurahans = doHttpCall(request);
                    for (HashMap kelurahan : kelurahans) {
                        Regions regions = new Regions();
                        regions.setParentCode(parent);
                        regions.setRegionCode(kelurahan.get("kode_dagri").toString());
                        regions.setRegionName(kelurahan.get("nama_dagri").toString());

                        insert(regions);
                    }
                }
            }
        }
    }


    /**
     * make sure we select it first and insert only if not exist
     *
     * @param regions
     */
    private void insert(Regions regions) {
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession(true);
            RegionsMapper regionsMapper = sqlSession.getMapper(RegionsMapper.class);

            Regions region = regionsMapper.getRegion(regions.getRegionCode());
            if(region == null) {
                regionsMapper.insert(regions);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}
