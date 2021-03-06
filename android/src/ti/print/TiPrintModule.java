/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2018 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.print.PrintHelper;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@Kroll.module(name = "TiPrint", id = "ti.print")
public class TiPrintModule extends KrollModule {

    // Standard Debugging variables
    private static final String LCAT = "TiPrintModule";
    private WebView mWebView;

    public TiPrintModule() {
        super();
    }

    @Kroll.onAppCreate
    public static void onAppCreate(TiApplication app) {

    }

    private void doWebViewPrint(String jobName, String html) {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(getActivity());
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                TiBaseActivity baseActivity = (TiBaseActivity) TiApplication.getAppRootOrCurrentActivity();
                PrintManager printManager = (PrintManager) baseActivity.getInitialBaseContext().getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

                printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                mWebView = null;
            }
        });

        webView.loadDataWithBaseURL("file:///android_asset/Resources/", html, "text/HTML", "UTF-8", null);
        mWebView = webView;
    }


    // Methods
    @Kroll.method
    public void printImage(KrollDict options) {
        String jobName = "";
        if (options.containsKeyAndNotNull("jobName")) {
            jobName = options.getString("jobName");
        }

        if (options.containsKeyAndNotNull("image")) {
            TiBaseActivity baseActivity = (TiBaseActivity) TiApplication.getAppRootOrCurrentActivity();
            TiBlob blob = TiConvert.toBlob(options.get("image"));
            PrintHelper photoPrinter = new PrintHelper(baseActivity.getInitialBaseContext());
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            Bitmap bitmap = blob.getImage();
            photoPrinter.printBitmap(jobName, bitmap);
        }
    }

    @Kroll.method
    public void printHTML(KrollDict options) {
        String jobName = "";
        if (options.containsKeyAndNotNull("jobName")) {
            jobName = options.getString("jobName");
        }

        if (options.containsKeyAndNotNull("html")) {
            doWebViewPrint(jobName, options.getString("html"));
        }
    }

    @Kroll.method
    public void printFile(KrollDict options) {
        // Get a PrintManager instance
        TiBaseActivity baseActivity = (TiBaseActivity) TiApplication.getAppRootOrCurrentActivity();
        PrintManager printManager = (PrintManager) baseActivity.getInitialBaseContext().getSystemService(Context.PRINT_SERVICE);
        final TiBaseFile file;

        String jobName = "";
        if (options.containsKeyAndNotNull("jobName")) {
            jobName = options.getString("jobName");
        }

        if (options.containsKeyAndNotNull(TiC.PROPERTY_URL)) {
            String url = TiConvert.toString(options.get(TiC.PROPERTY_URL));
            String absUrl = resolveUrl(null, url);
            file = TiFileFactory.createTitaniumFile(new String[]{absUrl}, false);

            printManager.print(jobName, new PrintDocumentAdapter() {
                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                    if (cancellationSignal.isCanceled()) {
                        callback.onLayoutCancelled();
                        return;
                    }

                    PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                    callback.onLayoutFinished(pdi, true);
                }

                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                    InputStream input = null;
                    OutputStream output = null;

                    try {

                        input = file.getInputStream();
                        output = new FileOutputStream(destination.getFileDescriptor());

                        byte[] buf = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = input.read(buf)) > 0) {
                            output.write(buf, 0, bytesRead);
                        }

                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (input != null && output != null) {
                            try {
                                input.close();
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, null);
        }
    }


}
