from PIL import Image, ImageChops
import os.path

def trim(im):
    bg = Image.new(im.mode, im.size, im.getpixel((0, 0)))
    diff = ImageChops.difference(im, bg)
    bbox = diff.getbbox()
    if bbox:
        return im.crop(bbox)

folders = ['char_main', 'char_side', 'char_evil']
names = [['paul', 'shin', 'jason', 'rick', 'sam'],
         ['heather'],
         ['mob', 'enemy']]
nums = range(1, 6)

for i in range(len(folders)):
  folder = folders[i]
  for name in names[i]:
    for j in nums:
      file_name = '..\\{}\\{}{}.png'.format(folder, name, j)
      if os.path.isfile(file_name):
        # retrieve image data
        img = Image.open(file_name)
        img = img.convert("RGBA")
        datas = img.getdata()

        # make light-gray to white pixels transparent
        newData = []
        for item in datas:
          if item[0] > 200 and item[1] > 200 and item[2] > 200:
            newData.append((255, 255, 255, 0))
          else:
            newData.append(item)
        img.putdata(newData)
        
        # trim excess whitespace
        img = trim(img)

        # replace old image with the transparent image
        img.save(file_name, "PNG")
        print('Finished:', file_name)

