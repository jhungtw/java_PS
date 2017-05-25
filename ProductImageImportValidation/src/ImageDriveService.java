import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ImageDriveService {
	
	private List AllImageInFolder = new ArrayList<String>();
	private List AllImageWithItemCodeInFolder= new ArrayList<String>();
	private List AllImageWithITempositionCodeInFolder= new ArrayList<String>();
	
	

	

	public void readFileListInImageDrive(String dirPath) {
		String itemCodePattern = "(\\d+).png";
		String itemPositionCodePattern = "(\\d+)-[1,2,3].png";
		// TDDO 1. read item/item position codes from T drive
		File dir = new File(dirPath);
	
		List<File> files = (List<File>) FileUtils.listFiles(dir, null, false);
		for (File file : files) {
			this.AllImageInFolder.add(file.getName());
			if (file.getName().matches(itemPositionCodePattern)) {
				this.AllImageWithITempositionCodeInFolder.add(file.getName());
			}

			if (file.getName().matches(itemCodePattern)) {
				this.AllImageWithItemCodeInFolder.add(file.getName());
			}
			//System.out.println("file: " + file.getCanonicalPath() + "--" + file.getName());
		}
		
		this.showListContent(AllImageInFolder);
		this.showListContent(AllImageWithItemCodeInFolder);
		this.showListContent(AllImageWithITempositionCodeInFolder);
	}





	public List getAllImageInFolder() {
		return AllImageInFolder;
	}





	public void setAllImageInFolder(List allImageInFolder) {
		AllImageInFolder = allImageInFolder;
	}





	public List getAllImageWithItemCodeInFolder() {
		return AllImageWithItemCodeInFolder;
	}





	public void setAllImageWithItemCodeInFolder(List allImageWithItemCodeInFolder) {
		AllImageWithItemCodeInFolder = allImageWithItemCodeInFolder;
	}





	public List getAllImageWithITempositionCodeInFolder() {
		return AllImageWithITempositionCodeInFolder;
	}





	public void setAllImageWithITempositionCodeInFolder(List allImageWithITempositionCodeInFolder) {
		AllImageWithITempositionCodeInFolder = allImageWithITempositionCodeInFolder;
	}





	private static void showListContent(List tDriveWithPosition) {
		// TODO Auto-generated method stub
	
		for (String tmp : (ArrayList<String>) tDriveWithPosition) {
			System.out.println("--> " + tmp);
	
		}
	}

}
