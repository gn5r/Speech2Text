/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.rd4c.speech2text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 *
 * @author gn5r
 */
public class GoogleSpeechAPI {

    private final String Host = "https://www.google.com/speech-api/v2/recognize?output=json&lang=ja-JP&key=";
    private final String APIKey;
    private final String filePATH;

    public GoogleSpeechAPI(String APIKey, String filePATH) {
        this.APIKey = APIKey;
        this.filePATH = filePATH;
    }

    /*    POSTリクエスト    */
    private String post(String url) throws Exception {
        /*    接続先URLのインスタンス生成    */
        URL urls = new URL(Host + url);
        HttpURLConnection con = (HttpURLConnection) urls.openConnection();
        String result = "";

        /*    ボディ送信を許可    */
        con.setDoInput(true);
        /*    レスポンスボディ受信を許可    */
        con.setDoOutput(true);
        /*    HTTPリクエストをPOSTに設定    */
        con.setRequestMethod("POST");
        /*    Content-Typeを設定    */
        con.setRequestProperty("Transfer-Encoding", "chunked");
        con.setRequestProperty("Content-Type", "audio/l16; rate=16000");
        con.setRequestProperty("AcceptEncoding", "gzip,deflate,sdch");

        /*    リクエストボディの作成    */
        PrintStream ps = new PrintStream(con.getOutputStream());

        /*    音声ファイルをbyte配列へ変換    */
        Path path = Paths.get(filePATH);
        byte[] buff = Files.readAllBytes(path);
        ps.write(buff);
        ps.close();
        /*    接続開始    */
        con.connect();

        /*    HTTP通信の確認    */
        final int statusCode = con.getResponseCode();

        switch (statusCode) {

            /*    レスポンスコード :200    */
            case HttpURLConnection.HTTP_OK:
                result = getResut(con);
                break;

            /*    レスポンスコード:201    */
            case HttpURLConnection.HTTP_CREATED:
                result = getResut(con);
                break;

            /*    レスポンスコード:401    */
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                result = "401";
                break;

            /*    レスポンスコード:400    */
            case HttpURLConnection.HTTP_BAD_REQUEST:
                result = "400";
                break;
        }

        con.disconnect();

        return result;
    }

    /*    レスポンスボディ取得    */
    private String getResut(HttpURLConnection con) throws Exception {

        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String body;

        /*    レスポンスボディの情報を1行ずつ読み取る    */
        while ((body = reader.readLine()) != null) {
            result.append(body);
        }
        reader.close();

        return result.toString();
    }

    /*    JSONをパースして必要な要素を取り出す    */
    private String parseJson(String json, String key) {
        JSONObject jsonObj = new JSONObject(json);
        return jsonObj.getString(key);
    }

    /*    音声データを送信し、認識結果の1件目の文字列を返す    */
    public String postGoogleAPI() throws Exception {
        String json = post(this.APIKey);
        String transcript, result;

        /*    レスポンスコード読取り    */
        switch (json) {
            case "401":
                System.out.println("APIKeyが間違っています");
                return "401";

            case "400":
                System.out.println("BAD REQUEST");
                return "400";
        }

        if (json.contains("{\"transcript\":\"") == false) {

            return result = "認識できません";
        }
        
        /*    音声認識の候補の１件目を取り出す    */
        transcript = json.substring(json.indexOf("{\"transcript\":\""));
        
        /*    jsonパース    */
        result = parseJson(transcript, "transcript");
        return result;
    }
}
