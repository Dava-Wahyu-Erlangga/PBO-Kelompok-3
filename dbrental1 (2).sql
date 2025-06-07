-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 07, 2025 at 08:13 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbrental1`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `idBarang` int(11) NOT NULL,
  `namaBarang` varchar(20) NOT NULL,
  `kategori` varchar(50) NOT NULL,
  `hargaSewa` decimal(10,2) NOT NULL,
  `tersedia` tinyint(1) NOT NULL DEFAULT 1,
  `stok` int(11) NOT NULL DEFAULT 0,
  `gambarBarang` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`idBarang`, `namaBarang`, `kategori`, `hargaSewa`, `tersedia`, `stok`, `gambarBarang`) VALUES
(1, 'Kursi Lipat', 'Furnitur', 10000.00, 1, 9, 'kursilipat.jpg'),
(2, 'Lampu Panggung', 'Elektronik', 30000.00, 1, 6, 'lampupanggung.jpg'),
(3, 'Panggung Portable', 'Peralatan', 500000.00, 1, 5, 'panggungportable.jpg'),
(4, 'Sound System', 'Elektronik', 750000.00, 1, 10, 'soundsystem.png'),
(5, 'Proyektor', 'Elektronik', 25000.00, 1, 8, 'proyektor.jpg'),
(8, 'Mikrofon', 'Peralatan', 100000.00, 1, 10, 'mikrofon.png');

-- --------------------------------------------------------

--
-- Table structure for table `pembayaran`
--

CREATE TABLE `pembayaran` (
  `idPembayaran` int(11) NOT NULL,
  `idPesanan` int(11) NOT NULL,
  `metodePembayaran` varchar(20) NOT NULL,
  `nomorRek` varchar(30) DEFAULT NULL,
  `namaPemilikRek` varchar(50) DEFAULT NULL,
  `tglPembayaran` date DEFAULT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pembayaran`
--

INSERT INTO `pembayaran` (`idPembayaran`, `idPesanan`, `metodePembayaran`, `nomorRek`, `namaPemilikRek`, `tglPembayaran`, `status`) VALUES
(32, 66, 'E-Wallet', '1234568', 'Erlin Haryanti', '2025-06-07', 'Lunas'),
(33, 67, 'E-Wallet', '1234', 'Faricha', '2025-06-07', 'Lunas'),
(34, 68, 'E-Wallet', '456', 'user', '2025-06-07', 'Lunas'),
(35, 69, 'E-Wallet', '678', 'test', '2025-06-07', 'Lunas'),
(36, 70, 'E-Wallet', '123', 'icha', '2025-06-08', 'Lunas');

-- --------------------------------------------------------

--
-- Table structure for table `pesanan`
--

CREATE TABLE `pesanan` (
  `idPesanan` int(11) NOT NULL,
  `idUser` int(11) NOT NULL,
  `idBarang` int(11) NOT NULL,
  `durasiSewa` int(5) NOT NULL,
  `totalHarga` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL,
  `jumlahUnit` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pesanan`
--

INSERT INTO `pesanan` (`idPesanan`, `idUser`, `idBarang`, `durasiSewa`, `totalHarga`, `status`, `jumlahUnit`) VALUES
(66, 8, 1, 1, 50000.00, 'Lunas', 5),
(67, 2, 3, 1, 500000.00, 'Lunas', 1),
(68, 2, 2, 1, 60000.00, 'Lunas', 2),
(69, 10, 1, 2, 20000.00, 'Lunas', 1),
(70, 2, 5, 1, 25000.00, 'Lunas', 1);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `idUser` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(30) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `alamat` text NOT NULL,
  `noTelp` varchar(15) DEFAULT NULL,
  `role` enum('admin','user') DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`idUser`, `username`, `password`, `nama`, `alamat`, `noTelp`, `role`) VALUES
(1, 'dava_aja', 'password123', 'dava wahyu', 'Jl. Merpati No. 10, Jakarta', '2147483647', 'user'),
(2, 'icha', '123', 'faricha dillia', 'Jl. Kenari No. 5, Bandung', '2147483647', 'user'),
(3, 'resyasya', 'password125', 'resya nisa', 'Jl. Kenari No. 5, Cikampek', '2147483647', 'user'),
(4, 'berlinlin', 'password126', 'erlin haryanti', 'Jl. Kenari No. 5, Semarang', '2147483647', 'user'),
(5, 'rahmat', 'password123', 'Rahmat Hidayat', 'Jakarta Barat', '081234567789', 'user'),
(6, 'ipul_pul', 'pw123', 'Syaipul Jamil', 'Boyolali', '0987767890', 'user'),
(7, 'resa_aja', 'pw124', 'resaresa', 'cikampek', '012873879379', 'user'),
(8, 'erlindwii', '123', 'erlinn', 'pluto', '102083080', 'user'),
(9, 'admin', '123', 'admin', 'kelompok3', '0812345678', 'admin'),
(10, 'test', '123', 'test', 'test', '08888888', 'user');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`idBarang`);

--
-- Indexes for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD PRIMARY KEY (`idPembayaran`),
  ADD KEY `fk_pesanan` (`idPesanan`);

--
-- Indexes for table `pesanan`
--
ALTER TABLE `pesanan`
  ADD PRIMARY KEY (`idPesanan`),
  ADD KEY `fk_user` (`idUser`),
  ADD KEY `fk_barang` (`idBarang`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`idUser`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `barang`
--
ALTER TABLE `barang`
  MODIFY `idBarang` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `pembayaran`
--
ALTER TABLE `pembayaran`
  MODIFY `idPembayaran` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `pesanan`
--
ALTER TABLE `pesanan`
  MODIFY `idPesanan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `idUser` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD CONSTRAINT `fk_pesanan` FOREIGN KEY (`idPesanan`) REFERENCES `pesanan` (`idPesanan`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `pesanan`
--
ALTER TABLE `pesanan`
  ADD CONSTRAINT `fk_barang` FOREIGN KEY (`idBarang`) REFERENCES `barang` (`idBarang`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
