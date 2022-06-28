package br.org.eldorado.hiaac.layout.firebase;

public interface FirebaseListener {

    public void onProgress(String message);

    public void onCompleted(String message);
}
