import imageio
import numpy as np
import pickle
import modules


network_num=8

networks=pickle.load(open("networks_dump_"+str(network_num)+".p","rb"))

to_test= imageio.imread("./training/2350507_bw.jpeg")

input=np.dstack((to_test,to_test,to_test))

for layers in networks:
    prev=layers.forward(input)
    prev=modules.sigmoid(prev)

prev.reshape((64,64,3))
imageio.imwrite("test.jpg",prev)
