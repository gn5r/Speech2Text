/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.rd4c.speech2text;

import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author pi
 */
public class MicroPhone {

    // リニアPCM 16bit 15000Hz × 10秒間 = 300000byte
    private final byte[] voiceData = new byte[300000];

    private AudioFormat audioFormat;
    private DataLine.Info info;
    private TargetDataLine target;

    public void init() throws LineUnavailableException {
        // リニアPCM 15000Hz 16bit モノラル 符号付き リトルエンディアン
        audioFormat = new AudioFormat(15000, 16, 1, true, false);
        // ターゲットデータラインを取得する
        info = new DataLine.Info(TargetDataLine.class, audioFormat);
        target = (TargetDataLine) AudioSystem.getLine(info);
        target.open(audioFormat);
    }

    /*    録音開始    */
    public void startRec() throws IOException {
        System.out.println("マイク入力開始...");
        target.start();
        AudioInputStream inputStream = new AudioInputStream(target);
        inputStream.read(voiceData, 0, voiceData.length);
    }

    /*    録音停止    */
    public void stopRec() {
        target.stop();
        target.close();
        System.out.println("マイク入力停止");
    }

    /*    録音データをWAVにして保存    */
    public File convertWav(String fileName) throws IOException {
        System.out.println("wavファイルへ変換します");

        File audioFile = new File(fileName + ".wav");
        ByteArrayInputStream arrayStream = new ByteArrayInputStream(voiceData);
        AudioInputStream inputStream = new AudioInputStream(arrayStream, audioFormat, voiceData.length);
        AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, audioFile);
        inputStream.close();
        arrayStream.close();

        return audioFile;
    }

}
