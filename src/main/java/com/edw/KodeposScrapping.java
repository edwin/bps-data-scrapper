package com.edw;

import com.edw.bean.Kodepos;
import com.edw.config.MyBatisSqlSessionFactory;
import com.edw.mapper.KodeposMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *     com.edw.KodeposScrapping
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 13 Des 2021 14:52
 */
public class KodeposScrapping {

    private SqlSession sqlSession = null;
    private KodeposMapper kodeposMapper  = null;

    private static final String BASE_URL = "https://sig.bps.go.id";

    private Logger logger = LogManager.getLogger(KodeposScrapping.class);

    public KodeposScrapping() {
    }

    public static void main(String[] args) throws IOException {
        KodeposScrapping kodeposScrapping = new KodeposScrapping();
        kodeposScrapping.doScrapping();
    }

    private void doScrapping() throws IOException {
        getProvinsis();
    }

    /**
     *  <pre> curl "https://sig.bps.go.id/rest-drop-down/getwilayah" </pre>
     */
    private void getProvinsis() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-drop-down/getwilayah")
                .get()
                .build();

        List<HashMap> hashMaps = doHttpCall(request);

        for (HashMap hashMap : hashMaps) {
            Kodepos kodepos = new Kodepos();
            kodepos.setProvinsi(hashMap.get("nama").toString());

            logger.info("start processing {}", hashMap.get("nama").toString());

            // get Kabupatens from Provinsi
            getKabupatens(hashMap.get("kode").toString(), kodepos);

            logger.info("done processing {}", hashMap.get("nama").toString());
        }
    }

    /**
     *  <pre> curl "https://sig.bps.go.id/rest-drop-down/getwilayah?level=kabupaten&parent=11" </pre>
     */
    private void getKabupatens(String parent, Kodepos kodepos) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-drop-down/getwilayah?level=kabupaten&parent="+parent)
                .get()
                .build();

        List<HashMap> hashMaps = doHttpCall(request);

        for (HashMap hashMap : hashMaps) {
            kodepos.setKabupaten(hashMap.get("nama").toString());

            // get Kecamatans from Kabupaten
            getKecamatans(hashMap.get("kode").toString(), kodepos);
        }
    }

    /**
     *  <pre> curl "https://sig.bps.go.id/rest-drop-down/getwilayah?level=kecamatan&parent=1101" </pre>
     */
    private void getKecamatans(String parent, Kodepos kodepos) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-drop-down/getwilayah?level=kecamatan&parent="+parent)
                .get()
                .build();

        List<HashMap> hashMaps = Collections.synchronizedList(doHttpCall(request));

        for (HashMap hashMap : hashMaps) {
            kodepos.setKecamatan(hashMap.get("nama").toString());
            getKelurahans(hashMap.get("kode").toString(), kodepos);
        }
    }

    /**
     *  <pre> curl "https://sig.bps.go.id/rest-bridging-pos/getwilayah?level=desa&parent=1101050" </pre>
     */
    private void getKelurahans(String parent, Kodepos kodepos) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/rest-bridging-pos/getwilayah?level=desa&parent="+parent)
                .get()
                .build();

        List<HashMap> hashMaps = Collections.synchronizedList(doHttpCall(request));

        for (final HashMap hashMap : hashMaps) {
            kodepos.setKelurahan(hashMap.get("nama_bps").toString());
            kodepos.setKodepos(hashMap.get("kode_pos").toString());

            insert(kodepos);
        }
    }

    private void insert(Kodepos kodepos) {
        try {
            sqlSession = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession(true);
            kodeposMapper = sqlSession.getMapper(KodeposMapper.class);

            kodeposMapper.insert(kodepos);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
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
}
