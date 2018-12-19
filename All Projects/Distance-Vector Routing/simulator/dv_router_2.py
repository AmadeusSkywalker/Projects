"""
Your awesome Distance Vector router for CS 168
"""

import sim.api as api
import sim.basics as basics

from dv_utils import PeerTable, PeerTableEntry, ForwardingTable, \
    ForwardingTableEntry

# We define infinity as a distance of 16.
INFINITY = 16

# A route should time out after at least 15 seconds.
ROUTE_TTL = 15


class DVRouter(basics.DVRouterBase):
    # NO_LOG = True  # Set to True on an instance to disable its logging.
    # POISON_MODE = True  # Can override POISON_MODE here.
    # DEFAULT_TIMER_INTERVAL = 5  # Can override this yourself for testing.

    def __init__(self):
        """
        Called when the instance is initialized.

        DO NOT remove any existing code from this method.
        """
        self.start_timer()  # Starts calling handle_timer() at correct rate.

        # Maps a port to the latency of the link coming out of that port.
        self.link_latency = {}

        # Maps a port to the PeerTable for that port.
        # Contains an entry for each port whose link is up, and no entries
        # for any other ports.
        self.peer_tables = {}
        
        #records the latest route advisement sent out of each port for each destination host
        self.history={}

        # Forwarding table for this router (constructed from peer tables).
        self.forwarding_table = ForwardingTable()
    
        #A dictonary of peertable for all the posioned routes
        self.posioned_tables={}

    def add_static_route(self, host, port):
        """
        Adds a static route to a host directly connected to this router.

        Called automatically by the framework whenever a host is connected
        to this router.

        :param host: the host.
        :param port: the port that the host is attached to.
        :returns: nothing.
        """
        # `port` should have been added to `peer_tables` by `handle_link_up`
        # when the link came up.
        assert port in self.peer_tables, "Link is not up?"
        
        # TODO: fill this in!
        self.peer_tables[port]=PeerTable()
        self.peer_tables[port][host]=PeerTableEntry(host,0,PeerTableEntry.FOREVER)
        self.update_forwarding_table()
        self.send_routes(force=False)

    def handle_link_up(self, port, latency):
        """
        Called by the framework when a link attached to this router goes up.

        :param port: the port that the link is attached to.
        :param latency: the link latency.
        :returns: nothing.
        """
        self.link_latency[port] = latency
        self.peer_tables[port] = PeerTable()
        # TODO: fill in the rest!
        for dest,TableEntry in self.forwarding_table.items():
            packet=basics.RoutePacket(dest,TableEntry.latency)
            if(TableEntry.latency>=INFINITY):
               packet.latency=INFINITY
            self.send(packet,port)

    def handle_link_down(self, port):
        """
        Called by the framework when a link attached to this router does down.

        :param port: the port number used by the link.
        :returns: nothing.
        """
        # TODO: fill this in!
        del self.link_latency[port]
        potential_unreach=[]
        for dest in self.peer_tables[port].keys():
            del self.peer_tables[port][dest]
            potential_unreach.append(dest)
        del self.peer_tables[port]
        self.update_forwarding_table();
        for dest in potential_unreach:
            if(dest not in self.forwarding_table):
                self.posioned_tables[dest]=PeerTableEntry(dest,INFINITY,api.current_time())
        self.send_routes(force=False)

    def handle_route_advertisement(self, dst, port, route_latency):
        """
        Called when the router receives a route advertisement from a neighbor.

        :param dst: the destination of the advertised route.
        :param port: the port that the advertisement came from.
        :param route_latency: latency from the neighbor to the destination.
        :return: nothing.
        """
        # TODO: fill this in!
        self.peer_tables[port][dst]=PeerTableEntry(dst,route_latency,api.current_time()+ROUTE_TTL)
        self.update_forwarding_table()
        if(dst in self.posioned_tables):
           del self.posioned_tables[dst]
        self.send_routes(force=False)
    
    def update_forwarding_table(self):
        """
        Computes and stores a new forwarding table merged from all peer tables.

        :returns: nothing.
        """
        self.forwarding_table.clear()  # First, clear the old forwarding table.

        # TODO: populate `self.forwarding_table` by combining peer tables.
        for port1, peerTable in self.peer_tables.items():
            for port2,PeerTableEntry in peerTable.items():
                distance=self.link_latency[port1]+PeerTableEntry.latency
                if port2 in self.forwarding_table:
                   if(distance<self.forwarding_table[port2].latency):
                      self.forwarding_table[port2]=ForwardingTableEntry(port2,port1,distance)
                else:
                      self.forwarding_table[port2]=ForwardingTableEntry(port2,port1,distance)
        

    def handle_data_packet(self, packet, in_port):
        """
        Called when a data packet arrives at this router.

        You may want to forward the packet, drop the packet, etc. here.

        :param packet: the packet that arrived.
        :param in_port: the port from which the packet arrived.
        :return: nothing.
        """
        # TODO: fill this in!
        if packet.dst in self.forwarding_table and self.forwarding_table[packet.dst].latency<INFINITY:
            if(self.forwarding_table[packet.dst].port!=in_port):
                self.send(packet,self.forwarding_table[packet.dst].port)
            
    

    def send_routes(self, force=False):
        """
        Send route advertisements for all routes in the forwarding table.

        :param force: if True, advertises ALL routes in the forwarding table;
                      otherwise, advertises only those routes that have
                      changed since the last advertisement.
        :return: nothing.
        """
        # TODO: fill this in!
        if(force==True):
            #print("FORCE IS TRUE")
            if(self.POISON_MODE):
               justSent=False
               for dest, PeerEntry in self.posioned_tables.items():
                   for neighbor in self.peer_tables.keys():
                       if(api.current_time()>=PeerEntry.expire_time):
                           self.send(basics.RoutePacket(dest,INFINITY),neighbor)
                           justSent=True
                       if(justSent):
                           self.posioned_tables[dest]=PeerTableEntry(dest,INFINITY,api.current_time()+2)
                           justSent=False
                       #del self.posioned_tables[dest]
               for dest,TableEntry in self.forwarding_table.items():
                   for neighbor in self.peer_tables.keys():
                       if(neighbor==TableEntry.port):
                           self.send(basics.RoutePacket(dest,INFINITY),neighbor)
                           if(not neighbor in self.history):
                               self.history[neighbor]={}
                           self.history[neighbor][dest]=INFINITY
                       #print("Updata history! ", " dest: ",dest, " neighbor: ",neighbor)
                       else:
                           packet=basics.RoutePacket(dest,TableEntry.latency)
                           if(TableEntry.latency>=INFINITY):
                               packet.latency=INFINITY
                           self.send(packet,neighbor)
                           if(not neighbor in self.history):
                               self.history[neighbor]={}
                           self.history[neighbor][dest]=packet.latency
        #                  print("Updata history! ", " dest: ",dest, " neighbor: ",neighbor)
            else:
               for dest,TableEntry in self.forwarding_table.items():
                   for neighbor in self.peer_tables.keys():
                       if(neighbor!=TableEntry.port):
                           packet=basics.RoutePacket(dest,TableEntry.latency)
                           if(TableEntry.latency>=INFINITY):
                               packet.latency=INFINITY
                           self.send(packet,neighbor)
                           if(not neighbor in self.history):
                               self.history[neighbor]={}
                           self.history[neighbor][dest]=packet.latency
                            #print("Updata history! ", " dest: ",dest, " neighbor: ",neighbor)
        else:
            #print("FORCE IS FALSE")
            if(self.POISON_MODE):
               justSent=False
               for dest, PeerEntry in self.posioned_tables.items():
                   for neighbor in self.peer_tables.keys():
                       if(api.current_time()>=PeerEntry.expire_time):
                           self.send(basics.RoutePacket(dest,INFINITY),neighbor)
                           justSent=True
                   if(justSent):
                           self.posioned_tables[dest]=PeerTableEntry(dest,INFINITY,api.current_time()+2)
                           justSent=False
                   #del self.posioned_tables[dest]
               for dest,TableEntry in self.forwarding_table.items():
                   for neighbor in self.peer_tables.keys():
                       if(neighbor==TableEntry.port):
                           if(neighbor not in self.history.keys() or dest not in self.history[neighbor].keys() or self.history[neighbor][dest]!=INFINITY):
                               self.send(basics.RoutePacket(dest,INFINITY),neighbor)
                               if(not neighbor in self.history):
                                  self.history[neighbor]={}
                               self.history[neighbor][dest]=INFINITY
                       #print("Updata history! ", " dest: ",dest, " neighbor: ",neighbor)
                       else:
                           packet=basics.RoutePacket(dest,TableEntry.latency)
                           if(TableEntry.latency>=INFINITY):
                               packet.latency=INFINITY
                           if(neighbor not in self.history.keys() or dest not in self.history[neighbor].keys() or self.history[neighbor][dest]!=packet.latency):
                               self.send(packet,neighbor)
                               if(not neighbor in self.history):
                                  self.history[neighbor]={}
                               self.history[neighbor][dest]=packet.latency
#print("Updata history! ", " dest: ",dest, " neighbor: ",neighbor)
            else:
                #print("test_consecutive goes here!");
               for dest,TableEntry in self.forwarding_table.items():
                   for neighbor in self.peer_tables.keys():
                       #print("dest is", dest)
                       #print("neighbor is", neighbor)
                       #print("TableEntry.port is",TableEntry.port)
                       if(neighbor!=TableEntry.port):
                           packet=basics.RoutePacket(dest,TableEntry.latency)
                           #print("We are trying to send")
                           #print("Packet dest is",packet.destination)
                           #print("packet latency is",packet.latency);
                           if(TableEntry.latency>=INFINITY):
                               packet.latency=INFINITY
                           if(neighbor not in self.history.keys()
                              or dest not in self.history[neighbor].keys()
                              or self.history[neighbor][dest]!=packet.latency
                              ):
                               #print("We did send")
                               self.send(packet,neighbor)
                               if(not neighbor in self.history):
                                  self.history[neighbor]={}
                               self.history[neighbor][dest]=packet.latency
                                   #print("Updata history! ", " dest: ",dest, " neighbor: ",neighbor)

    def expire_routes(self):
        """
        Clears out expired routes from peer tables; updates forwarding table
        accordingly.
        """
        # TODO: fill this in!
        potential_unreach=[]
        for port,peerTable in self.peer_tables.items():
            for dst in peerTable.keys():
                if peerTable[dst].expire_time<=api.current_time():
                    del peerTable[dst]
                    potential_unreach.append(dst)
        self.update_forwarding_table();
        for dest in potential_unreach:
            #print("made unavailable",dest)
            if(dest not in self.forwarding_table):
                self.posioned_tables[dest]=PeerTableEntry(dest,INFINITY,api.current_time())
        
        #for dest, TableEntry in self.posioned_tables.items():
        #    if TableEntry.expire_time<=api.current_time():
        #        del TableEntry
        #             #self.send_routes(force=False)

            
    def handle_timer(self):
        """
        Called periodically.

        This function simply calls helpers to clear out expired routes and to
        send the forwarding table to neighbors.
        """
        self.expire_routes()
        self.send_routes(force=True)

    # Feel free to add any helper methods!
