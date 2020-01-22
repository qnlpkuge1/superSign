package com.lmxdawn.api.admin.util;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IPAUtils {
    /**
     * 读取ipa
     */
    public static Map<String, String> readIPA(String ipaURL) {
        Map<String, String> map = new HashMap<>();
        File file = new File(ipaURL);
        try {
            InputStream is = new FileInputStream(file);
            InputStream is2 = new FileInputStream(file);
            ZipInputStream zipIns = new ZipInputStream(is);
            ZipInputStream zipIns2 = new ZipInputStream(is2);
            ZipEntry ze;
            ZipEntry ze2;
            InputStream infoIs = null;
            NSDictionary rootDict = null;
            String icon = null;
            String fullIcon = null;
            while ((ze = zipIns.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
                    String name = ze.getName();
                    if (null != name &&
                            name.toLowerCase().contains(".app/info.plist")) {
                        ByteArrayOutputStream _copy = new
                                ByteArrayOutputStream();
                        int chunk = 0;
                        byte[] data = new byte[1024];
                        while (-1 != (chunk = zipIns.read(data))) {
                            _copy.write(data, 0, chunk);
                        }
                        infoIs = new ByteArrayInputStream(_copy.toByteArray());
                        rootDict = (NSDictionary) PropertyListParser.parse(infoIs);

                        //我们可以根据info.plist结构获取任意我们需要的东西
                        //比如下面我获取图标名称，图标的目录结构请下面图片
                        //获取图标名称
                        NSDictionary iconDict = (NSDictionary) rootDict.get("CFBundleIcons");

                        while (null != iconDict) {
                            if (iconDict.containsKey("CFBundlePrimaryIcon")) {
                                NSDictionary CFBundlePrimaryIcon = (NSDictionary) iconDict.get("CFBundlePrimaryIcon");
                                if (CFBundlePrimaryIcon.containsKey("CFBundleIconName")) {
                                    NSString CFBundleIconName = (NSString) CFBundlePrimaryIcon.get("CFBundleIconName");
                                    icon = CFBundleIconName.getContent();
                                    if (icon.contains(".png")) {
                                        icon = icon.replace(".png", "");
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            //根据图标名称下载图标文件到指定位置
            int index = 0;
            while ((ze2 = zipIns2.getNextEntry()) != null) {
                if (!ze2.isDirectory()) {
                    String name = ze2.getName();
                    if (name.contains(icon.trim())) {
                        System.out.println(name);
                        if (index == 0) {
                            String fileName = file.getParent() + "/icon.png";
                            extractIcon(zipIns2, fileName);
                        } else if (name.contains("3x")) {
                            String fileName = file.getParent() + "/fullIcon.png";
                            extractIcon(zipIns2, fileName);
                            break;
                        }
                        index++;
                    }
                }
            }
            // 应用包名
            NSString parameters = (NSString) rootDict.get("CFBundleIdentifier");
            map.put("package", parameters.toString());
            //应用名称
            parameters = (NSString) rootDict.get("CFBundleDisplayName");
            map.put("appName", parameters.toString());
            // 应用版本名
            parameters = (NSString) rootDict.objectForKey("CFBundleShortVersionString");
            map.put("version", parameters.toString());
            //应用版本号
            parameters = (NSString) rootDict.get("CFBundleVersion");
            map.put("versionCode", parameters.toString());

            /////////////////////////////////////////////////
            infoIs.close();
            is.close();
            zipIns.close();

        } catch (Exception e) {
            map.put("code", "fail");
            map.put("error", "读取ipa文件失败");
        }
        return map;
    }

    private static void extractIcon(ZipInputStream zipIns2, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        int chunk = 0;
        byte[] data = new byte[1024];
        while (-1 != (chunk = zipIns2.read(data))) {
            fos.write(data, 0, chunk);
        }
        fos.close();
    }

}
