public class Menu {
    private final String nama;
    private long harga;
    private final String kategori;

    public Menu(String nama, long harga, String kategori) {
        if (nama == null || nama.isBlank() || harga <= 0 || (!"makanan".equals(kategori) && !"minuman".equals(kategori))) {
            throw new IllegalArgumentException("Data menu tidak valid.");
        }

        this.nama = nama.trim();
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getNama() {
        return nama;
    }

    public long getHarga() {
        return harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setHarga(long harga) {
        if (harga <= 0) throw new IllegalArgumentException("Harga harus positif.");
        this.harga = harga;
    }
}
