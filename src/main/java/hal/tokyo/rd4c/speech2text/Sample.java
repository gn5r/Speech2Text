/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hal.tokyo.rd4c.speech2text;

import java.io.File;

/**
 *
 * @author gn5r
 */
public class Sample {

    public static void main(String[] args) throws Exception {
        MicroPhone microPhone = new MicroPhone();
        microPhone.init();
        microPhone.startRec();
        microPhone.stopRec();

        File data = microPhone.convertWav("sample");

        GoogleSpeechAPI googleSpeech = new GoogleSpeechAPI(args[0]);
        googleSpeech.setFilePATH(data.getPath());
        String result = googleSpeech.postGoogleAPI();
        System.out.println("認識結果:" + result);

        Speaker speaker = new Speaker();
        speaker.openFile(data.getPath());
        speaker.playSE();
        speaker.stopSE();
    }
}
