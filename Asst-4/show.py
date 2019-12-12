import imageio
import numpy as np
import pickle
import modules


network_num=15

networks_r=pickle.load(open("networks_r_dump_"+str(network_num)+".p","rb"))

networks_g=pickle.load(open("networks_g_dump_"+str(network_num)+".p","rb"))

networks_b=pickle.load(open("networks_b_dump_"+str(network_num)+".p","rb"))

training=pickle.load(open("x_truth.p","rb"))

truth=pickle.load(open("y_truth.p","rb"))

for i in range(0,3):

    #input=np.dstack((training[i],training[i],training[i])).flatten()

    input=training[i]

    input = input.reshape((input.size,1))

    prev_r=input
    prev_g = input
    prev_b = input
    print(training[i])
    print("next")
    print(input)

    for layers in networks_r:
        prev_r=layers.forward(prev_r)
        prev_r=modules.sigmoid(prev_r)

    for layers in networks_g:
        prev_g=layers.forward(prev_g)
        prev_g=modules.sigmoid(prev_g)

    for layers in networks_b:
        prev_b=layers.forward(prev_b)
        prev_b=modules.sigmoid(prev_b)

    prev_r=prev_r   *256
    prev_g = prev_g * 256
    prev_b = prev_b * 256
    print(prev_r)
    print(prev_g)
    print(prev_b)
    prev_r = prev_r.reshape((64, 64))
    prev_g = prev_g.reshape((64, 64))
    prev_b = prev_b.reshape((64, 64))
    prev=np.dstack((prev_r,prev_g,prev_b))
    prev=prev.reshape((64,64,3))
    imageio.imwrite("bw"+str(i)+".jpg",training[i])
    imageio.imwrite("test"+str(i)+".jpg",prev)
    imageio.imwrite("real" + str(i) + ".jpg", truth[i])

