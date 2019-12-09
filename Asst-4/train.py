import imageio
import numpy as np
from glob import glob
import os
from datetime import datetime
import tqdm
import random
import pickle
import modules

training_folder="./training/"
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


def forward_pro(x_data,networks):
    memory={}
    i=0
    for layer in networks:
        if(networks[0]==layer):
            lo=layer.forward(x_data)
            memory[str(i) + "_prev"] = x_data
        else:
            lo=layer.forward(prev)
            memory[str(i) + "_prev"] = prev
        memory[str(i) + "_middle"]=lo
        prev=modules.sigmoid(lo)
        memory[str(i) + "_after"] = prev
        print(prev.shape)
        print(lo.shape)
        
        i=i+1
    return prev, memory,i


def back_pro(dLoss,memory,networks,i):
    i=i-1
    for layers in reversed(networks):
        after=memory[str(i)+"_middle"]
        print(str(i)+"_middle")
        print(str(i)+"_prev")
        print(after)
        d_prev=modules.sigmoid_backward(dLoss,after)
        before=memory[str(i)+"_prev"]
        print(before)
        print(len(before))
        print(len(d_prev))
        dLoss=layers.backward(d_prev)
        i=i-1
    return networks



def begin_training(x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data):
    networks=[]
    basesize = x_data[0].size*3
    print(basesize)
    networks.append(modules.Dense_layer(basesize,basesize,learning_rate=0.5))
    #print(1)
    networks.append(modules.Dense_layer(basesize, basesize,learning_rate=0.5))
    #print(2)
    networks.append(modules.Dense_layer(basesize,basesize,learning_rate=0.5))
    #print(3)
    #Forward Propogation

    for p in range(0, len(x_data)):
        x_to_train=x_data[p]++x_data[p]++x_data[p] #copy 3 times for R,G,B channel
        y_pred,memory,i =forward_pro(x_to_train,networks)
        #y_pred_image=y_pred.reshape(64,64,3)
        print(y_pred)
        y_data_to_compare=y_data[p].flatten()
        MSE = np.square(np.subtract(y_data_to_compare, y_pred)).mean() #accuracy

        dMSE=np.subtract(y_data_to_compare,y_pred)

        #y_back=dMSE.reshape(basesize*3) #linearize the stuff

        #Start backward propogation
        networks=back_pro(dMSE,memory,networks,i)
        print(MSE)

    pickle.dump(networks,open("networks_dump_"+str(now)+".p", "wb"))
    #try 1-> Linearize and use 4 dense layer each with 3 densely connected layers

if(os.path.exists("x_truth.p")):
    x_truth=pickle.load(open("x_truth.p","rb"))
    y_truth=pickle.load(open("y_truth.p","rb"))
else:
    x_truth,y_truth,file_order=read_images()
    pickle.dump(x_truth,open("x_truth.p","wb"))
    pickle.dump(y_truth,open("y_truth.p","wb"))
    pickle.dump(file_order,open("file_order.p","wb"))
print(len(x_truth))
print(len(y_truth))
training_list,validation_list,test_list=generate_validate_set(len(x_truth))
x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data=prepare_data(x_truth,y_truth,training_list,validation_list,test_list)
write_current_configuration(training_list,validation_list,test_list)
begin_training(x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data)
