package com.suyang.settings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfficeSettings {
    @Value("${office.store}")
    private String store;
    @Value("${office.max-size}")
    private long maxSize;
    @Value("${office.converter-url}")
    private String converterUrl;
    @Value("${office.tempstore-url}")
    private String tempstoreUrl;
    @Value("${office.api-url}")
    private String apiUrl;
    @Value("${office.preloader-url}")
    private String preloaderUrl;
    @Value("${office.viewed-docs}")
    private String viewDocs;
    @Value("${office.edited-docs}")
    private String editDocs;
    @Value("${office.convert-docs}")
    private String convertDocs;

    public OfficeSettings() {
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public String getConverterUrl() {
        return converterUrl;
    }

    public void setConverterUrl(String converterUrl) {
        this.converterUrl = converterUrl;
    }

    public String getTempstoreUrl() {
        return tempstoreUrl;
    }

    public void setTempstoreUrl(String tempstoreUrl) {
        this.tempstoreUrl = tempstoreUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getPreloaderUrl() {
        return preloaderUrl;
    }

    public void setPreloaderUrl(String preloaderUrl) {
        this.preloaderUrl = preloaderUrl;
    }


    public String getViewDocs() {
        return viewDocs;
    }

    public void setViewDocs(String viewDocs) {
        this.viewDocs = viewDocs;
    }

    public String getEditDocs() {
        return editDocs;
    }

    public void setEditDocs(String editDocs) {
        this.editDocs = editDocs;
    }

    public String getConvertDocs() {
        return convertDocs;
    }

    public void setConvertDocs(String convertDocs) {
        this.convertDocs = convertDocs;
    }

}
