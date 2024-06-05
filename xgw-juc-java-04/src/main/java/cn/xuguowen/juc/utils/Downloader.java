package cn.xuguowen.juc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Downloader {
    public static List<String> download() throws IOException {
        // http://www.w3school.com.cn/example/html5/mov_bbb.mp4
        // https://www.w3schools.com/html/movie.mp4
        HttpURLConnection conn = (HttpURLConnection) new URL("http://www.w3school.com.cn/example/html5/mov_bbb.mp4").openConnection();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static void main(String[] args) throws IOException {
        List<String> download = download();
        System.out.println(download);
    }
}
