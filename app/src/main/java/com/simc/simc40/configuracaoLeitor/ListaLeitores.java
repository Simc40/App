package com.simc.simc40.configuracaoLeitor;

import com.simc.simc40.R;

import java.util.LinkedHashMap;

public interface ListaLeitores {
    String UHF_RFID_Reader_1128 = "UHF RFID Reader 1128";
    String UHF_RFID_Reader_i300 = "UHF RFID Reader i300";
    String QR_CODE = "QR Code";
    String LHF_RFID_Reader_Raspberry_Prototype = "LHF RFID Protótipo Raspberry";
    String LHF_RFID_Reader_Arduino_Prototype = "LHF RFID_Protótipo Arduino";

    LinkedHashMap<String, Integer> listaLeitores = new LinkedHashMap<String, Integer>(){{
        put(UHF_RFID_Reader_1128, R.drawable.eleven_twenty_eight);
        put(UHF_RFID_Reader_i300, R.drawable.i300);
        put(QR_CODE, R.drawable.qrcode);
        put(LHF_RFID_Reader_Raspberry_Prototype, R.drawable.raspberry);
        put(LHF_RFID_Reader_Arduino_Prototype, R.drawable.arduino);
    }};
}