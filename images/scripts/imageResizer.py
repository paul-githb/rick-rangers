from PIL import Image
import os.path

heights = [1100, 1200]
height_folders = [['char_main', 'char_side'], ['char_main', 'char_evil']]
height_names = [[['jason', 'paul', 'sam'], ['heather']], [['rick', 'shin'], ['mob', 'enemy']]]

for i in range(len(heights)):
  new_height = heights[i]
  folders = height_folders[i]
  names   = height_names[i]
  for j in range(len(folders)):
    folder = folders[j]
    for name in names[j]:
      for k in range(1, 6):
        file_name = '..\\{}\\{}{}.png'.format(folder, name, k)
        if os.path.isfile(file_name):
          img = Image.open(file_name)
          percent = new_height / float(img.size[1])
          new_width = int(img.size[0] * percent)
          img = img.resize((new_width, new_height), Image.ANTIALIAS)
          img.save(file_name)
          print('Finished:', file_name)

