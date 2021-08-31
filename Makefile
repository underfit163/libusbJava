run:all
	sudo java -cp commons-lang3-3.8.1.jar:libusb4java-1.3.0-linux-x86-64.jar:usb4java-1.3.0.jar:usb4java-javax-1.3.0.jar:usb-api-1.0.2.jar:. Main
all:
	sudo javac -cp commons-lang3-3.8.1.jar:libusb4java-1.3.0-linux-x86-64.jar:usb4java-1.3.0.jar:usb4java-javax-1.3.0.jar:usb-api-1.0.2.jar:javax.usb.properties:. Main.java



