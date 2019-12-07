import numpy as np


def sigmoid(Z):
    return 1 / (1 + np.exp(-Z))

def sigmoid_backward(dA, Z):
    sig = sigmoid(Z)
    return dA * sig * (1 - sig)

class Dense_layer:
    def __init__(self, input_units, output_units, learning_rate=0.5):
        # f(x) = Weights*Inputs + Some constants

        self.weights = np.random.normal(loc=0.0,scale=np.sqrt(2 / (input_units + output_units)),size=(input_units, output_units))
        self.biases = np.zeros(output_units) #W[0]
        self.learning_rate = learning_rate

    def forward(self, inputs):
        # f(x) = Weights*Inputs + Some constants

        # input shape: [batch, input_units]
        # output shape: [batch, output units]

        return np.dot(inputs, self.weights) + self.biases

    def backward(self, inputs, grad_output):
        grad_input = np.dot(grad_output, self.weights.T)

        # compute gradient with weights and biases
        grad_weights = np.dot(inputs.T, grad_output)
        grad_biases = grad_output.mean(axis=0) * inputs.shape[0]

        if grad_weights.shape == self.weights.shape and grad_biases.shape == self.biases.shape:
            print(
                "Something Wrong" + self.weights.shape + "," + str(grad_weights.shape) + "," + self.biases.shape + "," +
                str(grad_biases.shape))

        # Here we perform a stochastic gradient descent step.
        self.weights = self.weights - self.learning_rate * grad_weights
        self.biases = self.biases - self.learning_rate * grad_biases

        return grad_input
