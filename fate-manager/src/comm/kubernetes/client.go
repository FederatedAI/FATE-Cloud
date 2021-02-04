package kubernetes

import (
	"bytes"
	"context"
	"flag"
	"fmt"
	"io"
	"io/ioutil"
	"path/filepath"
	"strconv"
	"strings"

	"fate.manager/comm/logging"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
)

const (
	POD_STATUS       = "Running"
	NAMESPACE_STATUS = "Active"
)

var ClientSet *KubeClient

/*
create clientset for connect to kubernetes cluster when
this package is importing into another package
*/
func init() {
	var err error
	if ClientSet == nil {
		ClientSet, err = NewKubeClient("")
		if err != nil {
			logging.Error("init kubernetes clientgo err[%s]", err.Error())
		}
	}
}

//basic struct of a clientset
type KubeClient struct {
	// client use to connect with k8s cluster
	clientset *kubernetes.Clientset
}

func NewKubeClient(path string) (*KubeClient, error) {
	var kubeconfig *string
	var configPath string
	if len(path) == 0 {
		if home := homedir.HomeDir(); home != "" {
			kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
		} else {
			kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
		}
	} else {
		kubeconfig = flag.String("kubeconfig", filepath.Join(configPath, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	}
	flag.Parse()

	// use the current context in kubeconfig
	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	if err != nil {
		return nil, fmt.Errorf("GetK8sInfoFromOutcluster err:[%s]", err.Error())
	}

	// create the clientset
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		return nil, fmt.Errorf("GetK8sInfoFromOutcluster err:[%s]", err.Error())
	}
	return &KubeClient{clientset}, nil
}

/*
Get all pods that belong to the specified namespace
If there is no namespace pass into this method, the return value
will be all the pods in the current kubernetes cluster
*/
func (k *KubeClient) GetPods(namespace string) (*v1.PodList, error) {
	pods, err := k.clientset.CoreV1().Pods(namespace).List(context.TODO(), metav1.ListOptions{})
	if err != nil {
		return nil, fmt.Errorf("GetK8sInfoFromOutcluster err:[%s]", err.Error())
	}
	return pods, nil
}

/*
Get all pods that belong to the specified namespace then filter pods with
parameter pattern, the return value will be a list of pod names which has the name that match the
parameter pattern.
*/
func (k *KubeClient) GetPodListWithPattern(namespace, pattern string) ([]string, error) {
	var podNameList []string
	pods, err := k.GetPods(namespace)
	if err != nil {
		return nil, fmt.Errorf("GetK8sInfoFromOutcluster err:[%s]", err.Error())
	}
	for _, item := range pods.Items {
		if strings.Contains(item.Name, pattern) && item.Status.Phase == POD_STATUS {
			podNameList = append(podNameList, item.Name)
		}
	}
	return podNameList, nil
}

//Get a single pod name with a specified name which contains pattern.
func (k *KubeClient) GetPodWithPattern(namespace, pattern string) (string, error) {
	var podName string
	pods, err := k.GetPods(namespace)
	if err != nil {
		return "", fmt.Errorf("GetK8sInfoFromOutcluster err:[%s]", err.Error())
	}
	for _, item := range pods.Items {
		if strings.Contains(item.Name, pattern) && item.Status.Phase == POD_STATUS {
			podName = item.Name
		}
	}
	return strings.TrimSpace(podName), nil
}

// Get pod with specified namespace and pod name.
func (k *KubeClient) GetPodWithNames(namespace, pod string) (*v1.Pod, error) {
	podinfo, err := k.clientset.CoreV1().Pods(namespace).Get(context.TODO(), pod, metav1.GetOptions{})
	if errors.IsNotFound(err) {
		return nil, fmt.Errorf("Pod %s in namespace %s not found\n", pod, namespace)
	} else if statusError, isStatus := err.(*errors.StatusError); isStatus {
		return nil, fmt.Errorf("Error getting pod %s in namespace %s: %v\n",
			pod, namespace, statusError.ErrStatus.Message)
	} else if err != nil {
		return nil, fmt.Errorf("GetK8sInfoFromOutcluster err:[%s]", err.Error())
	} else {
		return nil, fmt.Errorf("Found pod %s in namespace %s\n", pod, namespace)
	}
	return podinfo, nil
}

// Get nodes list
func (k *KubeClient) GetNodes() ([]v1.Node, error) {
	nodeList, err := k.clientset.CoreV1().Nodes().List(context.TODO(), metav1.ListOptions{})
	if err != nil {
		return nil, fmt.Errorf("list nodes error[%s]", err.Error())
	}
	return nodeList.Items, nil
}

// Get nodes list except those contain parameter nodeName.
func (k *KubeClient) GetNodesWithoutSpecNode(nodeName string) ([]v1.Node, error) {
	var nodes []v1.Node
	nodeList, err := k.GetNodes()
	if err != nil {
		return nil, fmt.Errorf("GetNodesWithoutMaster err[%s]", err.Error())
	}
	for _, node := range nodeList {
		var needFilter bool
		for k, _ := range node.Labels {
			if strings.Contains(k, nodeName) {
				needFilter = true
			}
		}
		if needFilter {
			continue
		}
		nodes = append(nodes, node)
	}
	return nodes, nil
}

// Set or update labels for a node.
func (k *KubeClient) SetLabelsForNode(nodes []v1.Node, labels map[string]string) error {
	for _, node := range nodes {
		var labelKey, labelVal string
		if nodeLabel, exists := labels[node.Name]; exists {
			labelKey = strings.Split(nodeLabel, "=")[0]
			labelVal = strings.Split(nodeLabel, "=")[1]
			node.Labels[labelKey] = labelVal
			_, err := k.clientset.CoreV1().Nodes().Update(context.TODO(), &node, metav1.UpdateOptions{})
			if err != nil {
				return fmt.Errorf("update node labels err[%s]", err.Error())
			}
		}
	}
	return nil
}

// Generate labels before set label for node.
func (k *KubeClient) GenerateFMNodeLabel(nodes []v1.Node, tag, ipType string) map[string]string {
	nodesLabels := make(map[string]string)
	for k, node := range nodes {
		for _, v := range node.Status.Addresses {
			if string(v.Type) == ipType {
				// kubectl label nodes workshop fm-node-0=10.192.162.56
				nodesLabels[node.Name] = strings.Join([]string{tag, strconv.Itoa(k), "=", v.Address}, "")
			}
		}
	}
	return nodesLabels
}

func (k *KubeClient) GetNodeLabelOfFM(nodes []v1.Node, labelPrefix string) string {
	var labelOfFM string
	for _, node := range nodes {
		for k, v := range node.Labels {
			if strings.Contains(k, labelPrefix) {
				labelOfFM = strings.Join([]string{k, v}, ":")
			}
		}
	}
	return labelOfFM
}

// Get pod log and write logs to file.
func (k *KubeClient) WriteLogsIntoFile(namespace, podname, logpath string, num int64) error {
	req := k.clientset.CoreV1().Pods(namespace).GetLogs(podname, &v1.PodLogOptions{TailLines: &num})
	podLogs, err := req.Stream(context.TODO())
	if err != nil {
		return fmt.Errorf("error in opening stream")
	}
	defer podLogs.Close()
	buf := new(bytes.Buffer)
	_, err = io.Copy(buf, podLogs)
	if err != nil {
		return fmt.Errorf("error in copy information from podLogs to buf")
	}
	ioutil.WriteFile(logpath, buf.Bytes(), 0644)
	return nil
}

func (k *KubeClient) CreateNamespace(name string) (*v1.Namespace, error) {
	// Create(ctx context.Context, namespace *v1.Namespace, opts metav1.CreateOptions) (*v1.Namespace, error)
	var namespace v1.Namespace
	namespace.Name = name
	ns, err := k.clientset.CoreV1().Namespaces().Create(context.TODO(), &namespace, metav1.CreateOptions{})
	if err != nil {
		return nil, fmt.Errorf("create namespace err[%s]", err.Error())
	}
	return ns, nil
}

// List all namespace which has the status of NAMESPACE_STATUS.
func (k *KubeClient) ListNamespaceWithPattern(pattern string) ([]string, error) {
	// List(ctx context.Context, opts metav1.ListOptions) (*v1.NamespaceList, error)
	var namespaceList []string
	nsList, err := k.clientset.CoreV1().Namespaces().List(context.TODO(), metav1.ListOptions{})
	if err != nil {
		return nil, fmt.Errorf("list namespaces err[%s]", err.Error())
	}
	for _, ns := range nsList.Items {
		if strings.Contains(ns.Name, pattern) && ns.Status.Phase == NAMESPACE_STATUS {
			namespaceList = append(namespaceList, ns.Name)
		}
	}
	return namespaceList, nil
}
