package com.habib.siratemustakeem.utils

import android.util.Log
import com.habib.siratemustakeem.MyApplication
import com.habib.siratemustakeem.models.Duwa
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import java.io.IOException
import java.io.InputStream

class Util {

    companion object {
        @JvmStatic
        fun readExcelFileFromAssets(): ArrayList<Duwa> {
            val duwasList: ArrayList<Duwa> = ArrayList<Duwa>()
            try {
                var myInput: InputStream? = null
                try {
                    val assetMgr = MyApplication.appContext?.resources?.assets
                    myInput = assetMgr?.open("myexcelsheet_2.xls")
                } catch (e1: IOException) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace()
                }

                //  open excel sheet
                //  myInput = assetManager.open("myexcelsheet.xls");
                // Create a POI File System object
                val myFileSystem = POIFSFileSystem(myInput)
                // Create a workbook using the File System
                val myWorkBook = HSSFWorkbook(myFileSystem)
                // Get the first sheet from workbook
                val mySheet: HSSFSheet = myWorkBook.getSheetAt(0)
                // We now need something to iterate through the cells.
                val rowIter: Iterator<Row> = mySheet.rowIterator()
                var rowno = 0
                // textView!!.append("\n")
                while (rowIter.hasNext()) {
                    Log.e("", " row no $rowno")
                    val myRow: HSSFRow = rowIter.next() as HSSFRow
                    if (rowno != 0) {
                        val cellIter: Iterator<Cell> = myRow.cellIterator()
                        var colno = 0
                        var sno = ""
                        var titleEng = ""
                        var titleUrdu = ""
                        var arabic = ""
                        var engTrn = ""
                        var urduTrn = ""
                        while (cellIter.hasNext()) {
                            val myCell: HSSFCell = cellIter.next() as HSSFCell
                            if (colno == 0) {
                                sno = myCell.toString()
                            } else if (colno == 1) {
                                titleEng = myCell.toString()
                            } else if (colno == 2) {
                                titleUrdu = myCell.toString()
                            } else if (colno == 3) {
                                arabic = myCell.toString()
                            } else if (colno == 5) {
                                engTrn = myCell.toString()
                            } else if (colno == 4) {
                                urduTrn = myCell.toString()
                            }
                            colno++
                            Log.e(
                                "",
                                " Index :" + myCell.getColumnIndex()
                                    .toString() + " -- " + myCell.toString()
                            )
                        }
                        // textView.append(sno + " -- " + titleEng + "  -- " + titleUrdu + "\n");
                        val duwa = Duwa()
                        duwa.id = sno
                        duwa.titleEnglish = titleEng
                        duwa.titleUrdu = titleUrdu
                        duwa.arabicTrn = arabic
                        duwa.urduTrn = urduTrn
                        duwa.englishTrn = engTrn
                        duwasList.add(duwa)
                    }
                    rowno++
                }
            } catch (e: Exception) {
                Log.e("", "error $e")
            }

            return duwasList;
        }


        @JvmStatic
        fun getKalmaListFromAssets(): ArrayList<Duwa> {
            val duwasList: ArrayList<Duwa> = ArrayList<Duwa>()
            try {
                var myInput: InputStream? = null
                try {
                    val assetMgr = MyApplication.appContext?.resources?.assets
                    myInput = assetMgr?.open("myexcelsheet_2.xls")
                } catch (e1: IOException) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace()
                }

                //  open excel sheet
                //  myInput = assetManager.open("myexcelsheet.xls");
                // Create a POI File System object
                val myFileSystem = POIFSFileSystem(myInput)
                // Create a workbook using the File System
                val myWorkBook = HSSFWorkbook(myFileSystem)
                // Get the first sheet from workbook
                val mySheet: HSSFSheet = myWorkBook.getSheetAt(1)
                // We now need something to iterate through the cells.
                val rowIter: Iterator<Row> = mySheet.rowIterator()
                var rowno = 0
                // textView!!.append("\n")
                while (rowIter.hasNext()) {
                    Log.e("", " row no $rowno")
                    val myRow: HSSFRow = rowIter.next() as HSSFRow
                    if (rowno != 0) {
                        val cellIter: Iterator<Cell> = myRow.cellIterator()
                        var colno = 0
                        var sno = ""
                        var titleEng = ""
                        var titleUrdu = ""
                        var arabic = ""
                        var engTrn = ""
                        var urduTrn = ""
                        while (cellIter.hasNext()) {
                            val myCell: HSSFCell = cellIter.next() as HSSFCell
                            if (colno == 0) {
                                sno = myCell.toString()
                            } else if (colno == 1) {
                                titleEng = myCell.toString()
                            } else if (colno == 2) {
                                titleUrdu = myCell.toString()
                            } else if (colno == 3) {
                                arabic = myCell.toString()
                            } else if (colno == 4) {
                                urduTrn = myCell.toString()
                            } else if (colno == 5) {
                                engTrn = myCell.toString()
                            }
                            colno++
                            Log.e(
                                "",
                                " Index :" + myCell.getColumnIndex()
                                    .toString() + " -- " + myCell.toString()
                            )
                        }
                        // textView.append(sno + " -- " + titleEng + "  -- " + titleUrdu + "\n");
                        val duwa = Duwa()
                        duwa.id = sno
                        duwa.titleEnglish = titleEng
                        duwa.titleUrdu = titleUrdu
                        duwa.arabicTrn = arabic
                        duwa.urduTrn = urduTrn
                        duwa.englishTrn = engTrn
                        duwasList.add(duwa)
                    }
                    rowno++
                }
            } catch (e: Exception) {
                Log.e("", "error $e")
            }

            return duwasList;
        }

        @JvmStatic
        fun getAytualKursiFromAssets(): ArrayList<Duwa> {
            val duwasList: ArrayList<Duwa> = ArrayList<Duwa>()
            try {
                var myInput: InputStream? = null
                try {
                    val assetMgr = MyApplication.appContext?.resources?.assets
                    myInput = assetMgr?.open("myexcelsheet_2.xls")
                } catch (e1: IOException) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace()
                }

                //  open excel sheet
                //  myInput = assetManager.open("myexcelsheet.xls");
                // Create a POI File System object
                val myFileSystem = POIFSFileSystem(myInput)
                // Create a workbook using the File System
                val myWorkBook = HSSFWorkbook(myFileSystem)
                // Get the first sheet from workbook
                val mySheet: HSSFSheet = myWorkBook.getSheetAt(2)
                // We now need something to iterate through the cells.
                val rowIter: Iterator<Row> = mySheet.rowIterator()
                var rowno = 0
                // textView!!.append("\n")
                while (rowIter.hasNext()) {
                    Log.e("", " row no $rowno")
                    val myRow: HSSFRow = rowIter.next() as HSSFRow
                    if (rowno != 0) {
                        val cellIter: Iterator<Cell> = myRow.cellIterator()
                        var colno = 0
                        var sno = ""
                        var titleEng = ""
                        var titleUrdu = ""
                        var arabic = ""
                        var engTrn = ""
                        var urduTrn = ""
                        while (cellIter.hasNext()) {
                            val myCell: HSSFCell = cellIter.next() as HSSFCell
                            if (colno == 0) {
                                sno = myCell.toString()
                            } else if (colno == 1) {
                                titleEng = myCell.toString()
                            } else if (colno == 2) {
                                titleUrdu = myCell.toString()
                            } else if (colno == 3) {
                                arabic = myCell.toString()
                            } else if (colno == 4) {
                                urduTrn = myCell.toString()
                            } else if (colno == 5) {
                                engTrn = myCell.toString()
                            }
                            colno++
                            Log.e(
                                "",
                                " Index :" + myCell.getColumnIndex()
                                    .toString() + " -- " + myCell.toString()
                            )
                        }
                        // textView.append(sno + " -- " + titleEng + "  -- " + titleUrdu + "\n");
                        val duwa = Duwa()
                        duwa.id = sno
                        duwa.titleEnglish = titleEng
                        duwa.titleUrdu = titleUrdu
                        duwa.arabicTrn = arabic
                        duwa.urduTrn = urduTrn
                        duwa.englishTrn = engTrn
                        duwasList.add(duwa)
                    }
                    rowno++
                }
            } catch (e: Exception) {
                Log.e("", "error $e")
            }

            return duwasList;
        }

        @JvmStatic
        fun getQunootFromAssets(): ArrayList<Duwa> {
            val duwasList: ArrayList<Duwa> = ArrayList<Duwa>()
            try {
                var myInput: InputStream? = null
                try {
                    val assetMgr = MyApplication.appContext?.resources?.assets
                    myInput = assetMgr?.open("myexcelsheet_2.xls")
                } catch (e1: IOException) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace()
                }

                //  open excel sheet
                //  myInput = assetManager.open("myexcelsheet.xls");
                // Create a POI File System object
                val myFileSystem = POIFSFileSystem(myInput)
                // Create a workbook using the File System
                val myWorkBook = HSSFWorkbook(myFileSystem)
                // Get the first sheet from workbook
                val mySheet: HSSFSheet = myWorkBook.getSheetAt(3)
                // We now need something to iterate through the cells.
                val rowIter: Iterator<Row> = mySheet.rowIterator()
                var rowno = 0
                // textView!!.append("\n")
                while (rowIter.hasNext()) {
                    Log.e("", " row no $rowno")
                    val myRow: HSSFRow = rowIter.next() as HSSFRow
                    if (rowno != 0) {
                        val cellIter: Iterator<Cell> = myRow.cellIterator()
                        var colno = 0
                        var sno = ""
                        var titleEng = ""
                        var titleUrdu = ""
                        var arabic = ""
                        var engTrn = ""
                        var urduTrn = ""
                        while (cellIter.hasNext()) {
                            val myCell: HSSFCell = cellIter.next() as HSSFCell
                            if (colno == 0) {
                                sno = myCell.toString()
                            } else if (colno == 1) {
                                titleEng = myCell.toString()
                            } else if (colno == 2) {
                                titleUrdu = myCell.toString()
                            } else if (colno == 3) {
                                arabic = myCell.toString()
                            } else if (colno == 4) {
                                urduTrn = myCell.toString()
                            } else if (colno == 5) {
                                engTrn = myCell.toString()
                            }
                            colno++
                            Log.e(
                                "",
                                " Index :" + myCell.getColumnIndex()
                                    .toString() + " -- " + myCell.toString()
                            )
                        }
                        // textView.append(sno + " -- " + titleEng + "  -- " + titleUrdu + "\n");
                        val duwa = Duwa()
                        duwa.id = sno
                        duwa.titleEnglish = titleEng
                        duwa.titleUrdu = titleUrdu
                        duwa.arabicTrn = arabic
                        duwa.urduTrn = urduTrn
                        duwa.englishTrn = engTrn
                        duwasList.add(duwa)
                    }
                    rowno++
                }
            } catch (e: Exception) {
                Log.e("", "error $e")
            }

            return duwasList;
        }
    }
}