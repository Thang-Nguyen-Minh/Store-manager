package END;
import java.io.Serializable;

public class MatHang implements Serializable {
	private static int counter = 1000;
	private int maHang;
	private String tenHang;
	private String nhomHang;
	private double giaBan;

	public MatHang(String tenHang, String nhomHang, double giaBan) {
		this.maHang = counter++;
		this.tenHang = tenHang;
		this.nhomHang = nhomHang;
		this.giaBan = giaBan;
	}

	public int getMaHang() {
		return maHang;
	}

	public String getTenHang() {
		return tenHang;
	}

	public void setTenHang(String tenHang) {
		this.tenHang = tenHang;
	}

	public String getNhomHang() {
		return nhomHang;
	}

	public void setNhomHang(String nhomHang) {
		this.nhomHang = nhomHang;
	}

	public double getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(double giaBan) {
		this.giaBan = giaBan;
	}
}
