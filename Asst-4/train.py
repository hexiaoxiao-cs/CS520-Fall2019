import imageio
import numpy as np
from glob import glob
import os
from datetime import datetime
import tqdm
import random
import pickle
import modules
from skimage.util.shape import view_as_blocks
training_folder="./flower/"
now = datetime.now()

def read_images():
    x_truth=[]
    y_truth=[]
    file_order=[]
    i=0
    print("Reading Images\n")
    for image in tqdm.tqdm(glob(training_folder+"*.jpg")):
        basename=os.path.basename(image)
        filename=os.path.splitext(basename)[0]
        x_truth.append(imageio.imread(training_folder+filename+"_bw.jpeg")/256)
        y_truth.append(imageio.imread(image)/256)

        file_order.append(filename)
        i=i+1
        if i>3001:
            break
    return x_truth, y_truth, file_order

def generate_validate_set(size, prob=0.1):
    validation_list=random.sample(range(size),int(size*prob*2))
    training_list = [x for x in list(range(len(validation_list))) if x not in validation_list]
    test_list=validation_list[:len(validation_list)//2]
    validation_list = validation_list[len(validation_list) // 2:]

    return training_list,validation_list,test_list


def write_current_configuration(validation_list,test_list,file_order):
    pickle.dump(validation_list,open("validate_list_"+str(now)+".p", "wb"))
    pickle.dump(test_list, open("test_list_" + str(now) + ".p", "wb"))
    pickle.dump(file_order, open("file_order" + str(now) + ".p", "wb"))

def normalize_image(x_image,y_image):
    x_image=x_image/256
    y_image=y_image/256
    return x_image,y_image

def prepare_data(x_truth,y_truth,training_list,validation_list,test_list):
    x_data=[]
    y_data=[]
    x_validation_data=[]
    y_validation_data = []
    x_test_data=[]
    y_test_data = []
    for i in range(len(x_truth)):
        if i in training_list:
            x_data.append(np.array(x_truth[i]).flatten())
            y_data.append(np.array(y_truth[i]))
        else:
            if i in validation_list:
                x_validation_data.append(np.array(x_truth[i]).flatten())
                y_validation_data.append(np.array(y_truth[i]))
            else:
                x_test_data.append(np.array(x_truth[i]).flatten())
                y_test_data.append(np.array(y_truth[i]))
    return x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data


def forward_pro_2(x_data, networks):
    memory = {}
    i = 0
    for layer in networks:
        if i == 0:
            lo = layer.forward(x_data)
            memory[str(i) + "_prev"] = x_data
            # print("forward_x_data_shape")
            # print(x_data.shape)
        else:
            lo = layer.forward(prev)
            memory[str(i) + "_prev"] = prev
            ##print("forward_prev_shape")
            # print(prev.shape)
        # print(lo.shape)
        memory[str(i) + "_middle"] = lo
        prev = modules.relu(lo)
        memory[str(i) + "_after"] = prev
        # print(prev.shape)
        # print(lo.shape)

        i = i + 1
    return prev, memory, i


def back_pro_2(dLoss, memory, networks, i):
    i = i - 1
    for layers in reversed(networks):
        after = memory[str(i) + "_middle"]
        # print(str(i)+"_middle")
        # print(str(i)+"_prev")
        # print("after")
        # print(after)
        d_prev = modules.relu_backward(dLoss, after)
        # print(d_prev)
        before = memory[str(i) + "_prev"]
        # print(before)
        # print(len(before))
        # print(len(d_prev))
        dLoss = layers.backward(d_prev)
        # print(d_prev)
        # print(layers.weights)
        i = i - 1
    return networks


def forward_pro(x_data,networks):
    memory={}
    i=0
    for layer in networks:
        if i==0:
            lo=layer.forward(x_data)
            memory[str(i)+"_prev"]=x_data
            #print("forward_x_data_shape")
            #print(x_data.shape)
        else:
            lo=layer.forward(prev)
            memory[str(i) + "_prev"] = prev
            ##print("forward_prev_shape")
            #print(prev.shape)
        #print("forward")
        #print(lo.shape)
        memory[str(i) + "_middle"]=lo
        prev=modules.sigmoid(lo)
        memory[str(i) + "_after"] = prev
        #print(prev.shape)
        #print(lo.shape)
        
        i=i+1
    return prev, memory,i


def back_pro(dLoss,memory,networks,i):
    i=i-1
    for layers in reversed(networks):
        after=memory[str(i)+"_middle"]
        #print(str(i)+"_middle")
        #print(str(i)+"_prev")
        # print("after")
        # print(after)
        d_prev=modules.sigmoid_backward(dLoss,after)
        #print(d_prev)
        before=memory[str(i)+"_prev"]
        #print(before)
        #print(len(before))
        #print(len(d_prev))
        dLoss=layers.backward(d_prev)
        #print(d_prev)
        #print(layers.weights)
        i=i-1
    return networks

# def begin_training_patch(x_data, y_data, x_validation_data, y_validation_data, x_test_data, y_test_data):
#     networks_r = []
#     networks_g = []
#     networks_b = []
#     lr = 0.1
#     basesize = 64 #8*8 patch
#     # print(basesize)
#     networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     # print(1)
#     networks_g.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     networks_g.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     networks_g.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     # print(2)
#     networks_b.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     networks_b.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     networks_b.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
#     # print(3)
#     #minibatch_size = 32
#     # Forward Propogation
#     for x in tqdm.tqdm(range(100)):
#         sum_r = 0
#         sum_g = 0
#         sum_b = 0
#         for p in range(0, 30):
#             patch = view_as_blocks(x_data[p], block_shape=(8, 8))
#             p
#             #x_to_train = x_data[p]  # copy 3 times for R,G,B channel
#             # print(x_to_train.shape)
#             # print(x_data[p].shape)
#             #x_to_train = x_to_train.reshape((basesize, 1))
#
#         batch=np.concatenate(batch,axis=-1)
#         batch_y = np.stack(batch_y,axis=-1)
#         # print(batch.shape)
#         # print(batch_y.shape)
#         # print(batch)
#         # print("data_prepared")
#         y_pred_r, memory_r, i_r = forward_pro(batch, networks_r)
#         y_pred_g, memory_g, i_g = forward_pro(batch, networks_g)
#         y_pred_b, memory_b, i_b = forward_pro(batch, networks_b)
#         y_r_pred=np.zeros((basesize,basesize))
#         y_g_pred=np.zeros((basesize,basesize))
#         y_b_pred=np.zeros((basesize,basesize))
#         dMSE_r_s=np.zeros((basesize,minibatch_size))
#         dMSE_g_s = np.zeros((basesize, minibatch_size))
#         dMSE_b_s = np.zeros((basesize, minibatch_size))
#         for p in range(0,minibatch_size):
#             y_r_pred += y_pred_r[:,p]
#             y_g_pred += y_pred_g[:, p]
#             y_b_pred += y_pred_b[:, p]
#             #a picture
#             MSE_r = np.square(
#                 np.subtract(batch_y[:, :, 0,p].reshape((basesize, 1)), y_pred_r)).mean()  # accuracy
#             MSE_g = np.square(np.subtract(batch_y[:, :, 1,p].reshape((basesize, 1)), y_pred_g)).mean()
#             MSE_b = np.square(np.subtract(batch_y[:, :, 2,p].reshape((basesize, 1)), y_pred_b)).mean()
#             dMSE_r = np.subtract(y_pred_r[p], batch_y[:, :, 0,p].reshape((basesize, 1)))
#             dMSE_g = np.subtract(y_pred_g[p], batch_y[:, :, 1,p].reshape((basesize, 1)))
#             dMSE_b = np.subtract(y_pred_b[p], batch_y[:, :, 2,p].reshape((basesize, 1)))
#             dMSE_r_s += dMSE_r
#             dMSE_g_s += dMSE_g
#             dMSE_b_s += dMSE_b
#             # print(dMSE_r.shape)
#             # print(dMSE_b.shape)
#             # print(dMSE_g.shape)
#             sum_r+=MSE_r
#             sum_g+=MSE_g
#             sum_b+=MSE_b
#
#
#         dMSE_r_s=dMSE_r_s/minibatch_size
#         dMSE_g_s = dMSE_g_s / minibatch_size
#         dMSE_b_s = dMSE_b_s / minibatch_size
#
#             # y_pred_image=y_pred.reshape(64,64,3)
#             # print(y_pred)
#         y_data_to_compare = y_data[p]
#
#             # print("dMSE_r")
#             # print(dMSE_r)
#             # print("dMSE_g")
#             # print(dMSE_g)
#             # print("dMSE_b")
#             # print(dMSE_b)
#             # y_back=dMSE.reshape(basesize*3) #linearize the stuff
#
#             # Start backward propogation
#         networks_r = back_pro(dMSE_r_s, memory_r, networks_r, i_r)
#         networks_g = back_pro(dMSE_g_s, memory_g, networks_g, i_g)
#         networks_b = back_pro(dMSE_b_s, memory_b, networks_b, i_b)
#             # print(MSE_r)
#             # print(MSE_g)
#             # print(MSE_b)
#         # sum_r += MSE_r
#         #         # sum_g += MSE_g
#         #         # sum_b += MSE_b
#             # print(y_pred)
#             # print(y_data_to_compare)
#         pickle.dump(networks_r, open("networks_r_dump_" + str(x) + ".p", "wb"))
#         pickle.dump(networks_g, open("networks_g_dump_" + str(x) + ".p", "wb"))
#         pickle.dump(networks_b, open("networks_b_dump_" + str(x) + ".p", "wb"))
#         print(sum_r / 30)
#         print(sum_g / 30)
#         print(sum_b / 30)
#


def begin_training_minibatch(x_data, y_data, x_validation_data, y_validation_data, x_test_data, y_test_data):
    networks_r = []
    networks_g = []
    networks_b = []
    lr = 0.1
    basesize = x_data[0].size
    # print(basesize)
    networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    # print(1)
    networks_g.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_g.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_g.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_g.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    # print(2)
    networks_b.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_b.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_b.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_b.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    # print(3)
    minibatch_size = 32
    # Forward Propogation
    for x in tqdm.tqdm(range(100)):
        sum_r = 0
        sum_g = 0
        sum_b = 0
        batch=[]
        batch_y=[]
        for p in range(0, minibatch_size):
            batch.append(x_data[p].reshape((basesize,1)))
            batch_y.append(y_data[p])
            #x_to_train = x_data[p]  # copy 3 times for R,G,B channel
            # print(x_to_train.shape)
            # print(x_data[p].shape)
            #x_to_train = x_to_train.reshape((basesize, 1))

        batch=np.concatenate(batch,axis=-1)
        batch_y = np.stack(batch_y,axis=-1)
        # print(batch.shape)
        # print(batch_y.shape)
        # print(batch)
        # print("data_prepared")
        y_pred_r, memory_r, i_r = forward_pro(batch, networks_r)
        y_pred_g, memory_g, i_g = forward_pro(batch, networks_g)
        y_pred_b, memory_b, i_b = forward_pro(batch, networks_b)
        y_r_pred=np.zeros((basesize,basesize))
        y_g_pred=np.zeros((basesize,basesize))
        y_b_pred=np.zeros((basesize,basesize))
        dMSE_r_s=np.zeros((basesize,minibatch_size))
        dMSE_g_s = np.zeros((basesize, minibatch_size))
        dMSE_b_s = np.zeros((basesize, minibatch_size))
        for p in range(0,minibatch_size):
            y_r_pred += y_pred_r[:,p]
            y_g_pred += y_pred_g[:, p]
            y_b_pred += y_pred_b[:, p]
            #a picture
            MSE_r = np.square(
                np.subtract(batch_y[:, :, 0,p].reshape((basesize, 1)), y_pred_r)).mean()  # accuracy
            MSE_g = np.square(np.subtract(batch_y[:, :, 1,p].reshape((basesize, 1)), y_pred_g)).mean()
            MSE_b = np.square(np.subtract(batch_y[:, :, 2,p].reshape((basesize, 1)), y_pred_b)).mean()
            dMSE_r = np.subtract(y_pred_r[p], batch_y[:, :, 0,p].reshape((basesize, 1)))
            dMSE_g = np.subtract(y_pred_g[p], batch_y[:, :, 1,p].reshape((basesize, 1)))
            dMSE_b = np.subtract(y_pred_b[p], batch_y[:, :, 2,p].reshape((basesize, 1)))
            dMSE_r_s += dMSE_r
            dMSE_g_s += dMSE_g
            dMSE_b_s += dMSE_b
            # print(dMSE_r.shape)
            # print(dMSE_b.shape)
            # print(dMSE_g.shape)
            sum_r+=MSE_r
            sum_g+=MSE_g
            sum_b+=MSE_b


        dMSE_r_s=dMSE_r_s/minibatch_size
        dMSE_g_s = dMSE_g_s / minibatch_size
        dMSE_b_s = dMSE_b_s / minibatch_size

            # y_pred_image=y_pred.reshape(64,64,3)
            # print(y_pred)
        y_data_to_compare = y_data[p]

            # print("dMSE_r")
            # print(dMSE_r)
            # print("dMSE_g")
            # print(dMSE_g)
            # print("dMSE_b")
            # print(dMSE_b)
            # y_back=dMSE.reshape(basesize*3) #linearize the stuff

            # Start backward propogation
        networks_r = back_pro(dMSE_r_s, memory_r, networks_r, i_r)
        networks_g = back_pro(dMSE_g_s, memory_g, networks_g, i_g)
        networks_b = back_pro(dMSE_b_s, memory_b, networks_b, i_b)
            # print(MSE_r)
            # print(MSE_g)
            # print(MSE_b)
        # sum_r += MSE_r
        #         # sum_g += MSE_g
        #         # sum_b += MSE_b
            # print(y_pred)
            # print(y_data_to_compare)
        pickle.dump(networks_r, open("networks_r_dump_" + str(x) + ".p", "wb"))
        pickle.dump(networks_g, open("networks_g_dump_" + str(x) + ".p", "wb"))
        pickle.dump(networks_b, open("networks_b_dump_" + str(x) + ".p", "wb"))
        print(sum_r / 30)
        print(sum_g / 30)
        print(sum_b / 30)


def begin_training(x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data):
    networks_r=[]
    networks_g = []
    networks_b = []
    lr=0.05
    basesize = x_data[0].size
    #print(basesize)
    networks_r.append(modules.Dense_layer(basesize,basesize,learning_rate=lr))
    networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    networks_r.append(modules.Dense_layer(basesize, basesize, learning_rate=lr))
    #print(1)
    networks_g.append(modules.Dense_layer(basesize,basesize,learning_rate=lr))
    networks_g.append(modules.Dense_layer(basesize,basesize,learning_rate=lr))
    networks_g.append(modules.Dense_layer(basesize,basesize,learning_rate=lr))
    #print(2)
    networks_b.append(modules.Dense_layer(basesize,basesize,learning_rate=lr))
    networks_b.append(modules.Dense_layer(basesize,basesize,learning_rate=lr))
    networks_b.append(modules.Dense_layer(basesize,basesize,learning_rate=lr))
    #print(3)
    minibatch_size=32
    #Forward Propogation
    for x in tqdm.tqdm(range(100)):
        sum_r=0
        sum_g = 0
        sum_b = 0
        for p in range(0, 30):

            x_to_train=x_data[p] #copy 3 times for R,G,B channel
            #print(x_to_train.shape)
            #print(x_data[p].shape)
            x_to_train=x_to_train.reshape((basesize,1))
            y_pred_r,memory_r,i_r =forward_pro(x_to_train,networks_r)
            y_pred_g, memory_g, i_g = forward_pro(x_to_train, networks_g)
            y_pred_b, memory_b, i_b = forward_pro(x_to_train, networks_b)
            #y_pred_image=y_pred.reshape(64,64,3)
            #print(y_pred)
            y_data_to_compare=y_data[p]
            MSE_r = np.square(np.subtract(y_data_to_compare[:,:,0].reshape((basesize,1)), y_pred_r)).mean() #accuracy
            MSE_g = np.square(np.subtract(y_data_to_compare[:, :, 1].reshape((basesize,1)), y_pred_g)).mean()
            MSE_b = np.square(np.subtract(y_data_to_compare[:, :, 2].reshape((basesize,1)), y_pred_b)).mean()
            dMSE_r=np.subtract(y_pred_r,y_data_to_compare[:,:,0].reshape((basesize,1)))
            dMSE_g =np.subtract(y_pred_g, y_data_to_compare[:, :, 1].reshape((basesize,1)))
            dMSE_b =np.subtract(y_pred_b, y_data_to_compare[:, :, 2].reshape((basesize,1)))
            # print("dMSE_r")
            # print(dMSE_r)
            # print("dMSE_g")
            # print(dMSE_g)
            # print("dMSE_b")
            # print(dMSE_b)
            #y_back=dMSE.reshape(basesize*3) #linearize the stuff

            #Start backward propogation
            networks_r=back_pro(dMSE_r,memory_r,networks_r,i_r)
            networks_g = back_pro(dMSE_g, memory_g, networks_g, i_g)
            networks_b = back_pro(dMSE_b, memory_b, networks_b, i_b)
            # print(MSE_r)
            # print(MSE_g)
            # print(MSE_b)
            sum_r+=MSE_r
            sum_g += MSE_g
            sum_b += MSE_b
            #print(y_pred)
            #print(y_data_to_compare)
        pickle.dump(networks_r, open("networks_r_dump_" + str(x) + ".p", "wb"))
        pickle.dump(networks_g, open("networks_g_dump_" + str(x) + ".p", "wb"))
        pickle.dump(networks_b, open("networks_b_dump_" + str(x) + ".p", "wb"))
        output_loss_r = forward_pro()


    #try 1-> Linearize and use 4 dense layer each with 3 densely connected layers

if(os.path.exists("x_truth.p")):
    x_truth=pickle.load(open("x_truth.p","rb"))
    y_truth=pickle.load(open("y_truth.p","rb"))
else:
    x_truth,y_truth,file_order=read_images()
    pickle.dump(x_truth,open("x_truth.p","wb"))
    pickle.dump(y_truth,open("y_truth.p","wb"))
    pickle.dump(file_order,open("file_order.p","wb"))
##print(len(x_truth))
#print(len(y_truth))
training_list,validation_list,test_list=generate_validate_set(len(x_truth))
x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data=prepare_data(x_truth,y_truth,training_list,validation_list,test_list)
#write_current_configuration(training_list,validation_list,test_list)
#begin_training(x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data)
begin_training_minibatch(x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data)
