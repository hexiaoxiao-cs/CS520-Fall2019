import imageio
import numpy as np
import pickle
import modules


network_num=8

networks=pickle.load(open("networks_dump_"+str(network_num)+".p","rb"))

to_test= imageio.imread("./training/2350507_bw.jpeg")/256


input=np.dstack((to_test,to_test,to_test)).flatten()

prev=input

for layers in networks:
    prev=layers.forward(prev)
    prev=modules.sigmoid(prev)

prev=prev*256
print(prev)
prev=prev.reshape((64,64,3))

imageio.imwrite("test.jpg",prev)
