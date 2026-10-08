# Panduan memahami project

## Alur satu request
Saat pengguna membuat task, React memanggil api.createTask lewat HTTP POST. Controller membaca JSON dan meneruskan data ke service. Service memvalidasi judul dan memastikan board ada. Repository menjalankan INSERT menggunakan parameter SQL, lalu membaca row yang tersimpan. Controller mengembalikan JSON dengan HTTP 201. React memuat ulang daftar melalui API tanpa refresh halaman.

## Keputusan yang harus bisa dijelaskan
- Dua service: Spring Boot pada 8081 dan Vite pada 5173; masing-masing memiliki dependency dan proses sendiri.
- Controller tidak menyimpan aturan bisnis. Service bisa diuji tanpa web server.
- JdbcTemplate memakai parameter SQL, bukan menggabungkan input pengguna ke query.
- MariaDB menyimpan data secara permanen. Restart Java tidak menghapus row.
- Foreign key menjamin board task benar-benar ada. ON DELETE CASCADE menangani penghapusan child secara atomik.
- Flyway membuat schema saat startup dan mencatat migrasi yang sudah diterapkan.
- Index (board_id, status) membantu query per board dengan atau tanpa filter status.
- Tests memakai repository mock supaya fokus pada aturan service. Mereka tidak membuktikan FK MariaDB; cek database nyata tetap diperlukan.
- Frontend menangani loading, empty, dan error. Error jaringan menjadi pesan yang bisa dibaca serta Retry.
- PATCH hanya mengubah status; tidak ada aturan urutan transisi karena brief tidak menentukan urutan wajib.

## Yang perlu  dilakukan di laptop
Pastikan Java 17, Maven, Node/npm dan MariaDB tersedia. Jalankan backend tests, backend, kemudian frontend.
