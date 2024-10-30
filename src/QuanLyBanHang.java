package END;
import java.io.Serializable;

public class QuanLyBanHang extends KhachHang {
	private int maHang;
	private int soLuong;

	public QuanLyBanHang(String maKH, String tenKH, String diaChi, String sdt, int maHang, int soLuong) {
		super(maKH, tenKH, diaChi, sdt);
		this.maHang = maHang;
		this.soLuong = soLuong;
	}

	public int getMaHang() {
		return maHang;
	}

	public void setMaHang(int maHang) {
		this.maHang = maHang;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}
}
