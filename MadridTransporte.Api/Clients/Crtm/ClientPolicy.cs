using System.ServiceModel;
using MadridTransporte.Api.Clients.Crtm.Generated;
using Microsoft.Extensions.ObjectPool;

namespace MadridTransporte.Api.Clients.Crtm;

public class ClientPolicy(IConfiguration config) : IPooledObjectPolicy<MultimodalInformationClient>
{
    private static MultimodalInformationClient CreateClient(IConfiguration config)
    {
        var endpoint =
            config["Crtm:Endpoint"]
            ?? "http://www.citram.es:8080/WSMultimodalInformation/MultimodalInformation.svc";
        var binding = new BasicHttpBinding
        {
            SendTimeout = TimeSpan.FromSeconds(config.GetValue("Crtm:TimeoutSeconds", 30)),
        };
        return new MultimodalInformationClient(binding, new EndpointAddress(endpoint));
    }

    public MultimodalInformationClient Create() => CreateClient(config);

    public bool Return(MultimodalInformationClient obj)
    {
        if (
            obj.State
            is CommunicationState.Faulted
                or CommunicationState.Closed
                or CommunicationState.Closing
        )
        {
            try
            {
                obj.Abort();
            }
            catch { }
            return false;
        }
        return true;
    }
}
