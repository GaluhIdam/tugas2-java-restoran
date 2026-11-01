import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final NumberFormat RUPIAH = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private static final long PELAYANAN = 20_000;
    private static final String MINUMAN_PROMO = "Es Teh Manis";
    private static Menu[] daftarMenu = {
            new Menu("Nasi Goreng", 30_000, "makanan"),
            new Menu("Mie Goreng", 25_000, "makanan"),
            new Menu("Ayam Bakar", 35_000, "makanan"),
            new Menu("Soto Ayam", 28_000, "makanan"),
            new Menu(MINUMAN_PROMO, 10_000, "minuman"),
            new Menu("Jus Jeruk", 15_000, "minuman"),
            new Menu("Kopi Susu", 20_000, "minuman"),
            new Menu("Air Mineral", 5_000, "minuman")
    };

    public static void main(String[] args) {
        RUPIAH.setMaximumFractionDigits(0);
        try {
            menuUtama();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("\nInput berakhir. Aplikasi ditutup.");
        }
    }

    private static void menuUtama() {
        int pilihan;
        do {
            System.out.println("\n=== RESTORAN NUSANTARA ===\n1. Pelanggan / pesan\n2. Pengelolaan menu\n0. Keluar");
            pilihan = bacaAngka("Pilih: ", 0, 2);
            switch (pilihan) {
                case 1:
                    prosesPesanan();
                    break;
                case 2:
                    kelolaMenu();
                    break;
                case 0:
                    System.out.println("Terima kasih. Sampai jumpa!");
                    break;
                default:
                    break;
            }
        } while (pilihan != 0);
    }

    private static String bacaTeks(String pesan) {
        while (true) {
            System.out.print(pesan);
            String teks = INPUT.nextLine().trim();
            if (!teks.isEmpty()) return teks;
            System.out.println("Input tidak boleh kosong.");
        }
    }

    private static int bacaAngka(String pesan, int minimum, int maksimum) {
        while (true) {
            try {
                int angka = Integer.parseInt(bacaTeks(pesan));
                if (angka >= minimum && angka <= maksimum) return angka;
            } catch (NumberFormatException e) {
            }
            System.out.println("Masukkan bilangan bulat " + minimum + " sampai " + maksimum + ".");
        }
    }

    private static boolean konfirmasi(String pesan) {
        while (true) {
            String jawaban = bacaTeks(pesan + " (Ya/Tidak): ");
            if (jawaban.equalsIgnoreCase("Ya")) return true;
            if (jawaban.equalsIgnoreCase("Tidak")) return false;
            System.out.println("Pilihan tidak valid. Ketik Ya atau Tidak.");
        }
    }

    private static String uang(long nilai) {
        return RUPIAH.format(nilai);
    }

    private static void tampilkanMenu() {
        System.out.println("\n=== DAFTAR MENU ===");
        String[] kategori = {"makanan", "minuman"};
        for (String kelompok : kategori) {
            System.out.println("\n-- " + kelompok.toUpperCase(Locale.ROOT) + " --");
            boolean ada = false;
            for (int i = 0; i < daftarMenu.length; i++) {
                Menu menu = daftarMenu[i];
                if (menu.getKategori().equals(kelompok)) {
                    System.out.printf("%d. %-24s %s%n", i + 1, menu.getNama(), uang(menu.getHarga()));
                    ada = true;
                }
            }
            if (!ada) System.out.println("Belum ada menu dalam kategori ini.");
        }
    }

    private static int cariMenu(String input) {
        for (int i = 0; i < daftarMenu.length; i++) {
            if (daftarMenu[i].getNama().equalsIgnoreCase(input)) return i;
        }
        try {
            int nomor = Integer.parseInt(input);
            if (nomor >= 1 && nomor <= daftarMenu.length) return nomor - 1;
        } catch (NumberFormatException e) {
        }
        return -1;
    }

    private static void prosesPesanan() {
        if (daftarMenu.length == 0) {
            System.out.println("Menu masih kosong. Silakan kembali setelah menu ditambahkan.");
            return;
        }

        int[] pesanan = new int[daftarMenu.length];
        tampilkanMenu();

        System.out.println("\nPromo: subtotal > Rp50.000, setiap 2 Es Teh Manis mendapat 1 gratis.");
        System.out.println("Subtotal > Rp100.000 mendapat diskon 10%. Ketik selesai untuk mencetak struk.");
        System.out.println("Ketik hapus untuk menghapus item pesanan, atau 0/kembali untuk membatalkan.");

        while (true) {
            tampilkanAksiPesanan();
            String input = bacaTeks("Pilihan Anda: ");
            if (input.equals("0") || input.equalsIgnoreCase("kembali")) {
                if (konfirmasi("Batalkan pesanan dan kembali?")) return;
                continue;
            }

            if (input.equalsIgnoreCase("selesai")) {
                if (hitungSubtotal(pesanan) == 0) {
                    System.out.println("Belum ada pesanan; tidak dikenakan biaya.");
                } else {
                    cetakStruk(pesanan);
                }
                return;
            }
            if (input.equalsIgnoreCase("hapus")) {
                hapusPesanan(pesanan);
                continue;
            }

            int indeks = cariMenu(input);
            if (indeks == -1) {
                System.out.println("Menu tidak ditemukan. Pilih menu yang terdaftar.");
                continue;
            }

            int jumlah = bacaAngka("Jumlah (1-1000000, 0 batal item): ", 0, 1_000_000);
            if (jumlah == 0) {
                System.out.println("Penambahan item dibatalkan.");
                continue;
            }
            if (pesanan[indeks] > 1_000_000 - jumlah) {
                System.out.println("Jumlah gabungan per item maksimal 1000000. Ulangi dengan jumlah lebih kecil.");
                continue;
            }

            pesanan[indeks] += jumlah;
            System.out.println(daftarMenu[indeks].getNama() + " ditambahkan. Jumlah: " + pesanan[indeks]);
        }
    }

    private static void tampilkanAksiPesanan() {
        System.out.println("\n=== AKSI PESANAN ===");
        System.out.println("Nomor/nama menu : tambah makanan atau minuman");
        System.out.println("selesai         : akhiri pemesanan dan cetak struk");
        System.out.println("hapus           : pilih item pesanan untuk dihapus");
        System.out.println("0 / kembali     : batalkan pesanan dan kembali ke menu utama");
    }

    private static void hapusPesanan(int[] pesanan) {
        if (hitungSubtotal(pesanan) == 0) {
            System.out.println("Pesanan masih kosong.");
            return;
        }

        for (int i = 0; i < pesanan.length; i++) {
            if (pesanan[i] > 0) System.out.println((i + 1) + ". " + daftarMenu[i].getNama() + " x" + pesanan[i]);
        }

        while (true) {
            int nomor = bacaAngka("Nomor item dihapus (0 kembali): ", 0, daftarMenu.length);
            if (nomor == 0) return;
            if (pesanan[nomor - 1] == 0) {
                System.out.println("Item tersebut belum dipesan.");
            } else {
                if (konfirmasi("Hapus seluruh jumlah item ini?")) pesanan[nomor - 1] = 0;
                return;
            }
        }
    }

    static long hitungSubtotal(int[] pesanan) {
        long subtotal = 0;
        for (int i = 0; i < pesanan.length; i++) subtotal += daftarMenu[i].getHarga() * pesanan[i];
        return subtotal;
    }

    static long hitungDiskon(long subtotal) {
        if (subtotal > 100_000) return subtotal / 10;
        return 0;
    }

    static long hitungPromo(int[] pesanan, long subtotal) {
        long potongan = 0;

        if (subtotal > 50_000) {
            for (int i = 0; i < pesanan.length; i++) {
                if (daftarMenu[i].getKategori().equals("minuman")) {
                    if (daftarMenu[i].getNama().equalsIgnoreCase(MINUMAN_PROMO)) {
                        potongan += (pesanan[i] / 2) * daftarMenu[i].getHarga();
                    }
                }
            }
        }

        return potongan;
    }

    private static void cetakStruk(int[] pesanan) {
        long subtotal = hitungSubtotal(pesanan);
        long diskon = hitungDiskon(subtotal);
        long promo = hitungPromo(pesanan, subtotal);
        long setelahPotongan = subtotal - diskon - promo;
        long pajak = subtotal / 10;
        long total = setelahPotongan + pajak + PELAYANAN;

        System.out.println("\n================ STRUK RESTORAN NUSANTARA ================");
        System.out.printf("%-24s %7s %14s %16s%n", "Item", "Jumlah", "Harga satuan", "Total item");

        for (int i = 0; i < pesanan.length; i++) {
            if (pesanan[i] > 0) {
                Menu menu = daftarMenu[i];
                System.out.printf("%-24s %7d %14s %16s%n", menu.getNama(), pesanan[i], uang(menu.getHarga()), uang(menu.getHarga() * pesanan[i]));
            }
        }

        System.out.println("Subtotal pesanan       : " + uang(subtotal));

        if (diskon > 0) System.out.println("Diskon 10% subtotal    : -" + uang(diskon));
        else System.out.println("Diskon 10%             : tidak berlaku (subtotal <= Rp100.000)");
        if (promo > 0)
            System.out.println("BOGO Es Teh Manis      : -" + uang(promo) + " (1 gratis per 2 gelas dipesan)");
        else if (subtotal > 50_000) System.out.println("BOGO                   : tidak ada pasangan Es Teh Manis");
        else System.out.println("BOGO                   : tidak berlaku (subtotal <= Rp50.000)");

        System.out.println("Setelah potongan       : " + uang(setelahPotongan));
        System.out.println("Pajak 10% subtotal     : " + uang(pajak));
        System.out.println("Biaya pelayanan        : " + uang(PELAYANAN));
        System.out.println("TOTAL BAYAR            : " + uang(total));
        System.out.println("=========================================================");
    }

    private static void kelolaMenu() {
        int pilihan;
        do {
            System.out.println("\n=== PENGELOLAAN MENU ===\n1. Lihat menu\n2. Tambah menu\n3. Ubah harga\n4. Hapus menu\n0. Kembali");
            pilihan = bacaAngka("Pilih: ", 0, 4);
            switch (pilihan) {
                case 1:
                    tampilkanMenu();
                    break;
                case 2:
                    tambahMenu();
                    break;
                case 3:
                    ubahHarga();
                    break;
                case 4:
                    hapusMenu();
                    break;
                case 0:
                    break;
                default:
                    break;
            }
        } while (pilihan != 0);
    }

    private static void tambahMenu() {
        boolean lanjut;
        do {
            String nama;

            while (true) {
                nama = bacaTeks("Nama menu baru (kembali untuk batal): ");
                if (nama.equalsIgnoreCase("kembali")) return;
                if (nama.equalsIgnoreCase("selesai") || nama.equalsIgnoreCase("hapus") || nama.matches("[+-]?[0-9]+")) {
                    System.out.println("Nama tidak boleh angka saja atau kata perintah.");
                    continue;
                }
                if (cariMenu(nama) == -1) break;
                System.out.println("Nama menu sudah digunakan.");
            }

            long harga = bacaAngka("Harga rupiah (1-10000000, 0 batal): ", 0, 10_000_000);
            if (harga == 0) return;

            System.out.println("1. Makanan\n2. Minuman\n0. Batal");

            int kategori = bacaAngka("Kategori: ", 0, 2);
            if (kategori == 0) return;

            String namaKategori = "makanan";
            if (kategori == 2) namaKategori = "minuman";

            daftarMenu = Arrays.copyOf(daftarMenu, daftarMenu.length + 1);
            daftarMenu[daftarMenu.length - 1] = new Menu(nama, harga, namaKategori);

            System.out.println("Menu berhasil ditambahkan.");
            lanjut = konfirmasi("Tambah menu lain?");
        } while (lanjut);
    }

    private static int pilihMenuKelola() {
        tampilkanMenu();

        if (daftarMenu.length == 0) {
            System.out.println("Tidak ada menu yang dapat diubah/dihapus.");
            return -1;
        }

        return bacaAngka("Nomor menu (0 kembali): ", 0, daftarMenu.length) - 1;
    }

    private static void ubahHarga() {
        int indeks = pilihMenuKelola();
        if (indeks < 0) return;

        Menu menu = daftarMenu[indeks];

        long harga = bacaAngka("Harga baru (1-10000000, 0 batal): ", 0, 10_000_000);
        if (harga == 0) return;

        if (konfirmasi("Ubah " + menu.getNama() + " dari " + uang(menu.getHarga()) + " menjadi " + uang(harga) + "?")) {
            menu.setHarga(harga);
            System.out.println("Harga berhasil diubah.");
        } else {
            System.out.println("Perubahan dibatalkan.");
        }
    }

    private static void hapusMenu() {
        int indeks = pilihMenuKelola();
        if (indeks < 0) return;

        if (konfirmasi("Hapus menu " + daftarMenu[indeks].getNama() + "?")) {
            Menu[] baru = new Menu[daftarMenu.length - 1];

            int tujuan = 0;
            for (int i = 0; i < daftarMenu.length; i++) {
                if (i != indeks) baru[tujuan++] = daftarMenu[i];
            }

            daftarMenu = baru;
            System.out.println("Menu berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
}
