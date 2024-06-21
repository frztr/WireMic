package com.example.wiremic;

public class DataController implements IDataController {
    private IModel model;

    public DataController()
    {
        model = ServiceProvider.getProvider().<IModel>getSingleton(IModel.class);
    }

    @Override
    public void SetIpAddress(String ip) {
        model.setIp(ip);
    }
}
