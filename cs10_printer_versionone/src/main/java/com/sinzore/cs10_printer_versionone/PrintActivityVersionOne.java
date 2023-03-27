package com.sinzore.cs10_printer_versionone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import vpos.apipackage.PosApiHelper;
import vpos.apipackage.PrintInitException;

public class PrintActivityVersionOne extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_version_one);

        StringBuffer sb = new StringBuffer();
        printBluetooth(sb);
    }

    PosApiHelper posApiHelper = PosApiHelper.getInstance();

    Print_Thread printThread = null;
    private boolean m_bThreadFinished = true;
    final int PRINT_TEST = 0;
    int ret = -1;

    private final static int ENABLE_RG = 10;
    private final static int DISABLE_RG = 11;
    private int RESULT_CODE = 0;

    int IsWorking = 0;

    public String tag = "Printer";

    public void printBluetooth(StringBuffer stringBuffer) {
        if (printThread != null && !printThread.isThreadFinished()) {
            Log.e(tag, "Thread is still running...");
            return;
        }
        printThread = new Print_Thread(PRINT_TEST, stringBuffer);
        printThread.start();
    }

    public class Print_Thread extends Thread {
        int type;
        StringBuffer stringBuffer;

        public boolean isThreadFinished() {
            return m_bThreadFinished;
        }

        public Print_Thread(int type, StringBuffer stringBuffer) {
            this.type = type;
            this.stringBuffer = stringBuffer;
        }

        public void run() {
            Log.d("Robert2", "Print_Thread[ run ] run() begin");
            Message msg = Message.obtain();
            Message msg1 = new Message();

            synchronized (this) {

                m_bThreadFinished = false;
                try {
                    ret = posApiHelper.PrintInit();
                } catch (PrintInitException e) {
                    e.printStackTrace();
                    int initRet = e.getExceptionCode();
                    Log.e(tag, "initRer : " + initRet);
                }

                Log.e(tag, "init code:" + ret);

                ret = 2;
                Log.e(tag, "getValue():" + ret);

                posApiHelper.PrintSetGray(ret);
                Log.e(tag, "PrintSetGray():" );
                {
                    RESULT_CODE = 0;
                }


                Log.d("Robert2", "Lib_PrnStart type= "+type );

                msg.what = DISABLE_RG;
                handler.sendMessage(msg);

                posApiHelper.PrintSetFont((byte) 24, (byte) 24, (byte) 0x00);
                posApiHelper.PrintStr(stringBuffer.toString());
                posApiHelper.PrintStr("================================\n");
                posApiHelper.PrintStr("\n\n\n\n\n\n\n\n\n\n");


                //SendMsg("Printing... ");
                final long starttime_long = System.currentTimeMillis();
                ret = posApiHelper.PrintStart();


                Log.e(tag, "PrintStart ret = " + ret);

                msg1.what = ENABLE_RG;
                handler.sendMessage(msg1);

                if (ret != 0) {
                    RESULT_CODE = -1;
                    Log.e("liuhao", "Lib_PrnStart fail, ret = " + ret);
                    if (ret == -1) {
                        SendMsg("No Print Paper");
                    } else if (ret == -2) {
                        SendMsg("Printer too hot ");
                    } else if (ret == -3) {
                        SendMsg("Device voltage voltage low ");
                    } else {
                        SendMsg("Printing fail");
                    }
                } else {
                    RESULT_CODE = 0;
                }
//                        } else {
//                            SendMsg("Printing failed");
//                        }

                m_bThreadFinished = true;

                Log.e(tag, "goToSleep2...");
            }
        }
    }

    public void SendMsg(String strInfo) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(PrintActivityVersionOne.this, strInfo, Toast.LENGTH_SHORT).show();
            }
        });
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("MSG", strInfo);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DISABLE_RG:
                    IsWorking = 1;
                    break;

                case ENABLE_RG:
                    IsWorking = 0;

                    break;
                default:
                    Bundle b = msg.getData();
                    String strInfo = b.getString("MSG");
                    break;
            }
        }
    };

}