import javax.usb.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;

public class Main {
//2.4
 public static void printDevices(UsbHub hub, String prefix) throws Exception{
     //2.3
     for(Object o: hub.getAttachedUsbDevices()){
         UsbDevice usbDev = (UsbDevice) o;
         UsbDeviceDescriptor devDesc = usbDev.getUsbDeviceDescriptor();
         System.out.println(String.format("%s%s (VID %04x PID %04x)",
                 prefix, usbDev.getProductString(),
                 devDesc.idVendor(),
                 devDesc.idProduct()
         ));

         if(usbDev.isUsbHub()){
             printDevices((UsbHub)usbDev, String.format("    %s", prefix));
         }
     }
 }
//3
 public static UsbDevice findDevices(UsbHub hub, int vendorId, int productId) throws Exception
 {
        for(Object o: hub.getAttachedUsbDevices()){
            UsbDevice usbDev = (UsbDevice) o;
            UsbDeviceDescriptor devDesc = usbDev.getUsbDeviceDescriptor();
            if ((devDesc.idVendor()&0xffff) == vendorId && (devDesc.idProduct()&0xffff) == productId)
            {
                return usbDev;
            }
            if (usbDev.isUsbHub())
            {
                usbDev = findDevices((UsbHub) usbDev, vendorId, productId);
                if (usbDev != null) return usbDev;
            }
        }
        return null;
 }


    public static void main(String []args) throws Exception {
        //2.1
        UsbServices services = UsbHostManager.getUsbServices();
        //2.2
        UsbHub usbHub = services.getRootUsbHub();
        //2.4
        printDevices(usbHub,"Device:");
        //3
        int vendorId = 0x09DA;
        int productId =  0x9090;
        UsbDevice device = findDevices(usbHub, vendorId,productId);
        UsbDeviceDescriptor devDesc = device.getUsbDeviceDescriptor();
        System.out.println(String.format("%s (VID %04x PID %04x)",
                 device.getProductString(),
                devDesc.idVendor(),
                devDesc.idProduct()));
        devDesc.idProduct();
        devDesc.idVendor();
        //4
        UsbConfiguration usbDevice =  device.getActiveUsbConfiguration();
        UsbInterface usbInterface = usbDevice.getUsbInterface((byte) 1);
        usbInterface.claim(new UsbInterfacePolicy() {
            @Override
            public boolean forceClaim(UsbInterface ui) {
                return true;
            }
        });
        //5.1
        UsbEndpoint usbEndpoint = usbInterface.getUsbEndpoint((byte)0x82);
        //5.2
        UsbPipe pipe = usbEndpoint.getUsbPipe();
        //6
        pipe.open();
        try
        {
            byte[] data =new byte[8];
            //Необходимо выводить сообщения
            //о текущем направлении движения мыши до тех пор, пока не будет нажата правая кнопка
            //мыши. При движении/нажатии колеса и нажатии левой кнопки мыши программа
            //не должна завершать работу.
            while(true) {
                int getData = pipe.syncSubmit(data);
                //System.out.println(Arrays.toString(data));
                //System.out.println("get count data:" + getData);
                int movX = data[2] & 0xf;
                movX = movX > 7 ? movX - 0x10 : movX;
                int movY = data[4] & 0xf;
                movY = movY > 7 ? movY - 0x10 : movY;
                if (movX > 0) {
                    System.out.println("Направо");//2(1) байт
                }
                if (movX < 0) {
                    System.out.println("Налево");//2(-1) и 3(-1) байт
                }
                if (movY > 0) {
                    System.out.println("Вниз");//4(1) байт
                }
                if (movY < 0) {
                    System.out.println("Вверх");//4(-1) и 5(-1) байт
                }
                if (BitSet.valueOf(new byte[]{ data[0]}).get(1)) {
                    System.out.println("Нажата правая кнопка мыши");
                    break;
                }
                Arrays.fill(data,(byte) 0);
            }
        }
        finally {
            pipe.close();
            usbInterface.release();
        }
    }
}
