from PIL import Image
import glob
import os

root_dir="./training/"

if not os.path.isdir(root_dir):
	os.mkdir(root_dir)

for image in glob.glob("./jpg/*.jpg"):
	b_name=os.path.basename(image)
	f_name=os.path.splitext(b_name)[0]
	img=Image.open(image)
	ratio=64/(min(img.size))
	img=img.resize((int(img.width*ratio),int(img.height*ratio)))
	img=img.crop(((img.width-64)/2,(img.height-64)/2,(img.width-64)/2+64,(img.height-64)/2+64))
	img_bw=img.convert(mode="L")
	img.save(root_dir+b_name,"JPEG")
	img_bw.save(root_dir+f_name+"_bw.jpeg","JPEG")
