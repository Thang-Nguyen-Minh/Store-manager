package END;
import java.io.Serializable;

public class HoaDon extends QuanLyBanHang {
	private double giaBan;
	private double thanhTien;

	public HoaDon(String maKH, String tenKH, String diaChi, String sdt, int maHang, int soLuong, double giaBan) {
		super(maKH, tenKH, diaChi, sdt, maHang, soLuong);
		this.giaBan = giaBan;
		this.thanhTien = soLuong * giaBan;
	}

	public double getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(double giaBan) {
		this.giaBan = giaBan;
		this.thanhTien = getSoLuong() * giaBan; // Cập nhật lại thành tiền
	}

	public double tinhTongTien() {
		return thanhTien;
	}
}
