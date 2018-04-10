import React, { Component } from 'react'
import ReactDOM from 'react-dom'
import { connect } from 'react-redux'
import { Table, Upload, Button, Icon, message, Divider, Modal, Input, Radio } from 'antd';
import * as FileAction from '../actions/fileaction'
const RadioGroup = Radio.Group;

class Main extends Component {

    selectedFile;
    isEdit = false;

    constructor(props) {
        super(props);

        this.state = {
            loading: false,
            data: [],
            visible: false,
            userId: '',
            userName: '',
            type: 'desktop',
        }

        this.handleUploadCallback = this.handleUploadCallback.bind(this);
        this.handlerOk = this.handlerOk.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.files) {
            this.setState({
                loading: false,
                data: nextProps.files
            })
        } else if (nextProps.error) {
            this.setState({ loading: false });
            message.error(nextProps.error);
        }
    }

    componentWillMount() {
        this.props.list();
        this.setState({ loading: true });
    }

    handlerOk() {
        if (this.state.userId === '') {
            message.error("用户Id不能为空！");
            return;
        }
        if (this.state.userName === '') {
            message.error("用户名称不能为空！");
            return;
        }
        this.setState({ visible: false });
        window.open("/api/file/edit?id=" + encodeURIComponent(this.selectedFile.id) + "&userId=" + encodeURIComponent(this.state.userId) + "&userName=" + encodeURIComponent(this.state.userName) + "&edit=" + this.isEdit + "&type=" + encodeURIComponent(this.state.type) + "&d=" + new Date().getTime());
    }

    handleCancel() {
        this.setState({ visible: false });
    }

    handleUploadCallback(info) {
        if (info.file.status === 'done') {
            this.props.list();
            this.setState({ loading: true });
        }
    }

    render() {
        const columns = [{
            title: '文件名',
            dataIndex: 'fileName',
            key: 'fileName',
        }, {
            title: '文件大小',
            dataIndex: 'fileSize',
            key: 'fileSize',
        }, {
            title: 'Action',
            key: 'action',
            render: (text, record) => (
                <span>
                    <a href="#" onClick={e => {
                        this.selectedFile = record;
                        this.setState({ visible: true })
                        this.isEdit = false;
                    }}>预览</a>
                    <Divider type="vertical" />
                    <a href="#" onClick={e => {
                        this.selectedFile = record;
                        this.setState({ visible: true })
                        this.isEdit = true;
                    }}>编辑</a>
                </span>
            )
        }];

        return (
            <div style={{ margin: 20 }}>
                <div>
                    <Upload name="file" onChange={this.handleUploadCallback} action="/api/file/upload" style={{ float: 'left' }} supportServerRender={true}>
                        <Button>
                            <Icon type="upload" /> 上传
                        </Button>
                    </Upload>
                </div>
                <Table rowKey="id" dataSource={this.state.data} columns={columns} />

                <Modal
                    title="输入用户信息"
                    visible={this.state.visible}
                    onOk={this.handlerOk}
                    onCancel={this.handleCancel}>
                    <div>
                        <Input placeholder="用户Id" value={this.state.userId} onChange={e => this.setState({ userId: e.target.value })} />
                        <Input placeholder="用户名称" value={this.state.userName} onChange={e => this.setState({ userName: e.target.value })} style={{ marginTop: 10 }} />
                        <RadioGroup onChange={e => this.setState({ type: e.target.value })} value={this.state.type} style={{ marginTop: 10 }} >
                            <Radio value="desktop">桌面</Radio>
                            <Radio value="mobile">手机</Radio>
                        </RadioGroup>
                    </div>
                </Modal>
            </div>);
    }
}

const mapStateToProps = (state) => {
    return state.FileReducer;
}

export default connect(mapStateToProps, FileAction)(Main)