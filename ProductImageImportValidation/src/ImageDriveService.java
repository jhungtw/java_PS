import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ImageDriveService {

	private List<String> AllImageInFolder = new ArrayList<String>();
	private List<String> AllImageWithItemCodeInFolder = new ArrayList<String>();
	private List<String> AllImageWithITempositionCodeInFolder = new ArrayList<String>();
	private List<String> AllImageWithWrongNameFormatInFolder = new ArrayList<String>();

	public void readFileListInImageDrive(String dirPath) {
		String itemCodePattern = "(\\d+).png";
		String itemPositionCodePattern = "(\\d+)-[1,2,3].png";
		// TDDO 1. read item/item position codes from T drive
		File dir = new File(dirPath);

		List<File> files = (List<File>) FileUtils.listFiles(dir, null, false);
		for (File file : files) {
			boolean added = false;
			String baseFileName = FilenameUtils.removeExtension(file.getName());
			
			if (file.getName().matches(itemPositionCodePattern)) {
				added = true;
				this.AllImageWithITempositionCodeInFolder.add(baseFileName);
				this.AllImageInFolder.add(baseFileName);
			}

			if (file.getName().matches(itemCodePattern)) {
				added = true;
				this.AllImageWithItemCodeInFolder.add(baseFileName);
				this.AllImageInFolder.add(baseFileName);
			}

			if (!added) {
				if (!baseFileName.equalsIgnoreCase("Thumbs")) {
					this.AllImageWithWrongNameFormatInFolder.add(baseFileName);
					this.AllImageInFolder.add(baseFileName);
				}

			}
			// System.out.println("file: " + file.getCanonicalPath() + "--" +
			// file.getName());
		}

		this.showListContent(AllImageInFolder);
		this.showListContent(AllImageWithItemCodeInFolder);
		this.showListContent(AllImageWithITempositionCodeInFolder);
		this.showListContent(AllImageWithWrongNameFormatInFolder);
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

	public List getAllImageWithWrongNameFormatInFolder() {
		return AllImageWithWrongNameFormatInFolder;
	}

	public void setAllImageWithWrongNameFormatInFolder(List allImageWithWrongNanmeFormatInFolder) {
		AllImageWithWrongNameFormatInFolder = allImageWithWrongNanmeFormatInFolder;
	}

	private static void showListContent(List<String> tDriveWithPosition) {
		// TODO Auto-generated method stub
		System.out.println(">>>>>>>>>>>>>>>>>>> ");
		for (String tmp : (ArrayList<String>) tDriveWithPosition) {
			System.out.println("--> " + tmp);

		}
	}

}
