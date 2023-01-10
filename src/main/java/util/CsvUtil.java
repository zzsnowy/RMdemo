package util;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CsvUtil {


    public static List<String[]> readCsv(String path) {

        List<String[]> data = new ArrayList<>();

        // 字符编码 设置下没坏处
        Charset charset = StandardCharsets.UTF_8;
        // 缓存大小 提高读取效率
        int bufferSize = 5 * 1024 * 1024;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), charset), bufferSize)) {
            String line;
            // 这里假设文件不是很大，不然这么些内存是不够用的
            while (Objects.nonNull(line = reader.readLine())) {
                data.add(line.split(","));
            }

        } catch (IOException e) {
            // IOException 就囊括了读取文件可能发生的全部意外
            e.printStackTrace();
        }
        return data;
    }

    public static void writeCsv(List<String[]> data, String sheetName, String filePath){

        // 指定字符编码
        Charset charset = StandardCharsets.UTF_8;
        // 指定缓存
        int bufferSize = 5 * 1024 * 1024;
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), charset), bufferSize
        )) {
            for (String[] datum : data) {
                writer.write(String.join(",", datum));
                // 换行
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // demo测试
    public static void main(String[] args) {

        List<String[]> lists = readCsv("/Users/zzsnowy/Desktop/class.csv");

        List<String[]> dataList = new ArrayList<>();
        for (int i = 1; i < lists.size(); i++) {
            dataList.add(lists.get(i));
        }

        writeCsv(dataList, "testSheet", "/Users/zzsnowy/Desktop/classTest.csv");
    }
}
