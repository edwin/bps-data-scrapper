package com.edw.bean;

import java.io.Serializable;

/**
 * <pre>
 *     com.edw.bean.Kodepos
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 13 Des 2021 15:09
 */
public class Kodepos implements Serializable {
    private Long id;
    private String kelurahan;
    private String kecamatan;
    private String kabupaten;
    private String provinsi;
    private String kodepos;

    public Kodepos() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten = kabupaten;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }

    @Override
    public String toString() {
        return "Kodepos{" +
                "id=" + id +
                ", kelurahan='" + kelurahan + '\'' +
                ", kecamatan='" + kecamatan + '\'' +
                ", kabupaten='" + kabupaten + '\'' +
                ", provinsi='" + provinsi + '\'' +
                ", kodepos='" + kodepos + '\'' +
                '}';
    }
}
